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
import org.thymeleaf.standard.expression.OgnlExpressionEvaluator;

import play.Logger;
import play.exceptions.ActionNotFoundException;
import play.mvc.ActionInvoker;
import play.mvc.Router;
import play.mvc.Http.Request;
import play.utils.Java;

/**
 * TODO DOCUMENT ME
 */

class ProcessorUtil {
    private static final Pattern PARAM_PATTERN = Pattern.compile("^\\s*.*?\\((.*)\\)\\s*$");

    private ProcessorUtil() {
    }
    
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
        
        Object obj = OgnlExpressionEvaluator.INSTANCE.evaluate(arguments, exp, arguments.getExpressionEvaluationRoot());
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
