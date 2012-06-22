/*
 * Copyright 2012 Satoshi Takata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package play.modules.thymeleaf.dialect;

import java.util.Collections;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import ognl.ClassResolver;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.IStandardExpressionEvaluator;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.ObjectUtils;

/**
 * the evaluator which offers the same function as OgnlExpressionEvaluator except the OgnlContext uses PlayClassResolver.
 * Copied mainly from org.thymeleaf.standard.expression.OgnlExpressionEvaluator.
 * 
 * @author Daniel Fern&aacute;ndez (Original Author)
 */

public class PlayOgnlExpressionEvaluator implements IStandardExpressionEvaluator {
    
    /** instance */
    public static final PlayOgnlExpressionEvaluator INSTANCE = new PlayOgnlExpressionEvaluator();
    private static final String OGNL_CACHE_PREFIX = "{ognl}";
    
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static boolean booleanFixApplied = false;
    
    private PlayClassResolver classResolver = new PlayClassResolver();
    
    /**
     * clears the Class cache
     */
    public void clearClassCache() {
        this.classResolver.clearClassCache();
    }
    
    public final Object evaluate(final Arguments arguments, final String expression, final Object root) {
       
        try {

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] OGNL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), expression);
            }

            
            Object expressionTree = null;
            ICache<String, Object> cache = null;
            
            final ICacheManager cacheManager = arguments.getConfiguration().getCacheManager();
            if (cacheManager != null) {
                cache = cacheManager.getExpressionCache();
                if (cache != null) {
                    expressionTree = cache.get(OGNL_CACHE_PREFIX + expression);
                }
            }
            
            if (expressionTree == null) {
                expressionTree = ognl.Ognl.parseExpression(expression);
                if (cache != null && null != expressionTree) {
                    cache.put(OGNL_CACHE_PREFIX + expression, expressionTree);
                }
            }
            
            
            final Map<String,Object> contextVariables = arguments.getBaseContextVariables();
            
            final Map<String,Object> additionalContextVariables =
                computeAdditionalContextVariables(arguments);
            if (additionalContextVariables != null && !additionalContextVariables.isEmpty()) {
                contextVariables.putAll(additionalContextVariables);
            }
            
            OgnlContext context = new OgnlContext(contextVariables);
            context.setClassResolver(this.classResolver);
            
            return Ognl.getValue(expressionTree, context, root);
            
        } catch (final OgnlException e) {
            throw new TemplateProcessingException(
                    "Exception evaluating OGNL expression: \"" + expression + "\"", e);
        }
        
    }

    /**
     * @param arguments
     * @return AdditionalContextVariables
     */
    protected Map<String,Object> computeAdditionalContextVariables(
            final Arguments arguments) {
        return Collections.emptyMap();
    }
    
    
    
    
    
    private PlayOgnlExpressionEvaluator() {
        super();
        if (!booleanFixApplied && shouldApplyOgnlBooleanFix()) {
            applyOgnlBooleanFix();
            booleanFixApplied = true;
        }
    }

    
    
    
    
    @Override
    public String toString() {
        return "OGNL";
    }
    
    
    
    /**
     * <p>
     *   Determines whether a fix should be applied to OGNL in order
     *   to evaluate Strings as booleans in the same way as 
     *   Thymeleaf does ('false', 'off' and 'no' are actually "false"
     *   instead of OGNL's default "true"). 
     * </p>
     * 
     * @return whether the OGNL boolean fix should be applied or not.
     */
    protected boolean shouldApplyOgnlBooleanFix() {
        return true;
    }
        
    
    
    private void applyOgnlBooleanFix() {
        
        try {
            
            final ClassLoader classLoader = 
                    ClassLoaderUtils.getClassLoader(PlayOgnlExpressionEvaluator.class);
            
            final ClassPool pool = new ClassPool(true);
            pool.insertClassPath(new LoaderClassPath(classLoader));

            final CtClass[] params = new CtClass[] { pool.get(Object.class.getName()) };
            
            // We must load by class name here instead of "OgnlOps.class.getName()" because
            // the latter would cause the class to be loaded and therefore it would not be
            // possible to modify it.
            final CtClass ognlClass = pool.get("ognl.OgnlOps");
            final CtClass fixClass = pool.get(PlayOgnlExpressionEvaluator.class.getName());
            
            final CtMethod ognlMethod = 
                    ognlClass.getDeclaredMethod("booleanValue", params);
            final CtMethod fixMethod = 
                    fixClass.getDeclaredMethod("fixBooleanValue", params);
            
            ognlMethod.setBody(fixMethod, null);
            
            // Pushes the class to the class loader, effectively making it
            // load the modified version instead of the original one. 
            ognlClass.toClass(classLoader, null);
            
        } catch (final Exception e) {
            // Any exceptions here will be consumed and converted into log messages.
            // An exception at this point could be caused by multiple situations that 
            // should not suppose the stop of the framework's initialization.
            // If the fix cannot not applied, an INFO message is issued and initialization
            // continues normally.
            if (logger.isTraceEnabled()) {
                logger.trace(
                        "Thymeleaf was not able to apply a fix on OGNL's boolean evaluation " +
                        "that would have enabled OGNL to evaluate Strings as booleans (e.g. in " +
                        "\"th:if\") in exactly the same way as Thymeleaf itself or Spring EL ('false', " +
                        "'off' and 'no' should be considered \"false\"). This did not stop the " +
                        "initialization process.", e);
            } else {
                logger.info(
                        "Thymeleaf was not able to apply a fix on OGNL's boolean evaluation " +
                        "that would have enabled OGNL to evaluate Strings as booleans (e.g. in " +
                        "\"th:if\") in exactly the same way as Thymeleaf itself or Spring EL ('false', " +
                        "'off' and 'no' should be considered \"false\"). This did not stop the " +
                        "initialization process. Exception raised was " + e.getClass().getName() + 
                        ": " + e.getMessage() + " [Set the log to TRACE for the complete exception stack trace]");
            }
        }
        
    }
        
        
    static boolean fixBooleanValue(final Object value) {
        return ObjectUtils.evaluateAsBoolean(value);
    }
  }
