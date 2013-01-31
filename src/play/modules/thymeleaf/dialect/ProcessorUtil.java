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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.standard.expression.OgnlVariableExpressionEvaluator;

import play.Logger;
import play.exceptions.ActionNotFoundException;
import play.mvc.ActionInvoker;
import play.mvc.Router;
import play.mvc.Http.Request;
import play.utils.Java;

/**
 * Utility class for processor expressions.
 */

class ProcessorUtil {
    private static final Pattern PARAM_PATTERN = Pattern.compile("^\\s*.*?\\((.*)\\)\\s*$");

    private ProcessorUtil() {
    }
    
    /**
     * Parses the playframework action expressions. The string inside "()" is
     * evaluated by OGNL in the current context.
     * 
     * @param arguments
     * @param attributeValue
     *            e.g. "Application.show(obj.id)"
     * @return parsed action path
     */
    @SuppressWarnings("unchecked")
    static String toActionString(final Arguments arguments, String attributeValue) {
        Matcher matcher = PARAM_PATTERN.matcher(attributeValue);
        if (!matcher.matches()) {
            return Router.reverse(attributeValue)
                         .toString();
        }

        String exp = matcher.group(1);
        if (StringUtils.isBlank(exp)) {
            return Router.reverse(attributeValue)
                         .toString();
        }

        Object obj = PlayOgnlVariableExpressionEvaluator.INSTANCE.evaluate(arguments.getConfiguration(), arguments, exp, false);
        if (obj instanceof Map) {
            return Router.reverse(attributeValue, (Map<String, Object>) obj)
                         .toString();

        }

        List<?> list = obj instanceof List ? (List<?>) obj : Arrays.asList(obj);
        
        Map<String, Object> paramMap = new HashMap<String, Object>();

        String extracted = StringUtils.substringBefore(attributeValue, "(");
        if (!extracted.contains(".")) {
            extracted = Request.current().controller + "." + extracted;
        }
        Object[] actionMethods = ActionInvoker.getActionMethod(extracted);
        String[] paramNames = null;
        try {
            paramNames = Java.parameterNames((Method) actionMethods[1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (paramNames.length < list.size()) {
            Logger.warn("param length unmatched. %s", Arrays.toString(paramNames));
            throw new ActionNotFoundException(attributeValue, null);
        }

        for (int i = 0; i < list.size(); i++) {
            paramMap.put(paramNames[i], list.get(i));
        }

        return Router.reverse(extracted, paramMap)
                     .toString();

    }
    
}
