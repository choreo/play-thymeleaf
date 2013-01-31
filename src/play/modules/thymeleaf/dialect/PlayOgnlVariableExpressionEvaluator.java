package play.modules.thymeleaf.dialect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.cache.ICache;
import org.thymeleaf.cache.ICacheManager;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.exceptions.TemplateProcessingException;
import org.thymeleaf.standard.expression.IStandardVariableExpressionEvaluator;
import org.thymeleaf.standard.expression.OgnlVariableExpressionEvaluator;
import org.thymeleaf.util.ClassLoaderUtils;
import org.thymeleaf.util.ObjectUtils;

import play.Play;

/**
 * the evaluator which offers the same function as OgnlVariableExpressionEvaluator except the OgnlContext uses PlayClassResolver.
 * Copied mainly from org.thymeleaf.standard.expression.OgnlVariableExpressionEvaluator.
 * 
 * @author Daniel Fern&aacute;ndez (Original Author)
 */
public class PlayOgnlVariableExpressionEvaluator 
        implements IStandardVariableExpressionEvaluator {
    
    
    private static final Logger logger = LoggerFactory.getLogger(PlayOgnlVariableExpressionEvaluator.class);

    /** instance */
    public static final PlayOgnlVariableExpressionEvaluator INSTANCE = new PlayOgnlVariableExpressionEvaluator();

    private static final String OGNL_CACHE_PREFIX = "{ognl}";


    private static boolean booleanFixApplied = false;
    
    private PlayClassResolver classResolver = new PlayClassResolver();

    
    /**
     * clears the Class cache
     */
    public void clearClassCache() {
        this.classResolver.clearClassCache();
    }

    
    public final Object evaluate(final Configuration configuration, 
            final IProcessingContext processingContext, final String expression, 
            final boolean useSelectionAsRoot) {
       
        try {

            if (logger.isTraceEnabled()) {
                logger.trace("[THYMELEAF][{}] OGNL expression: evaluating expression \"{}\" on target", TemplateEngine.threadIndex(), expression);
            }

            
            Object expressionTree = null;
            ICache<String, Object> cache = null;
            
            if (configuration != null) {
                final ICacheManager cacheManager = configuration.getCacheManager();
                if (cacheManager != null) {
                    cache = cacheManager.getExpressionCache();
                    if (cache != null) {
                        expressionTree = cache.get(OGNL_CACHE_PREFIX + expression);
                    }
                }
            }
            
            if (expressionTree == null) {
                expressionTree = ognl.Ognl.parseExpression(expression);
                if (cache != null && null != expressionTree) {
                    cache.put(OGNL_CACHE_PREFIX + expression, expressionTree);
                }
            }

            
            final Map<String,Object> contextVariables = new HashMap<String, Object>();
            
            final Map<String,Object> expressionObjects = processingContext.getExpressionObjects();
            if (expressionObjects != null) {
                contextVariables.putAll(expressionObjects);
            }
            
            final Map<String,Object> additionalContextVariables = computeAdditionalContextVariables(processingContext);
            if (additionalContextVariables != null && !additionalContextVariables.isEmpty()) {
                contextVariables.putAll(additionalContextVariables);
            }
            
            final Object evaluationRoot = 
                    (useSelectionAsRoot?
                            processingContext.getExpressionSelectionEvaluationRoot() :
                            processingContext.getExpressionEvaluationRoot());

            
            OgnlContext context = new OgnlContext(contextVariables);
            context.setClassResolver(this.classResolver);
            
            return Ognl.getValue(expressionTree, context, evaluationRoot);
            //return Ognl.getValue(expressionTree, contextVariables, evaluationRoot);
            
        } catch (final OgnlException e) {
            throw new TemplateProcessingException(
                    "Exception evaluating OGNL expression: \"" + expression + "\"", e);
        }
        
    }



    
    /**
     * Meant to be overwritten
     * @param processingContext
     * @return additional variables
     */
    protected Map<String,Object> computeAdditionalContextVariables(
            final IProcessingContext processingContext) {
        return Collections.emptyMap();
    }
    
    
    
    
    
    /**
     * 
     */
    protected PlayOgnlVariableExpressionEvaluator() {
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
        
    
    
    private static void applyOgnlBooleanFix() {
        
        try {
            
            final ClassLoader classLoader = Play.classloader; 
                    //ClassLoaderUtils.getClassLoader(OgnlVariableExpressionEvaluator.class);
            
            final ClassPool pool = new ClassPool(true);
            pool.insertClassPath(new LoaderClassPath(classLoader));

            final CtClass[] params = new CtClass[] { pool.get(Object.class.getName()) };
            
            // We must load by class name here instead of "OgnlOps.class.getName()" because
            // the latter would cause the class to be loaded and therefore it would not be
            // possible to modify it.
            final CtClass ognlClass = pool.get("ognl.OgnlOps");
            final CtClass fixClass = pool.get(PlayOgnlVariableExpressionEvaluator.class.getName());
            
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
