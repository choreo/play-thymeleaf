package thymeleaf.dialect;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.processor.attr.AbstractSingleAttributeModifierAttrProcessor;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor.ModificationType;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.OgnlExpressionEvaluator;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
import org.thymeleaf.standard.processor.attr.AbstractStandardSingleAttributeModifierAttrProcessor;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.thymeleaf.util.PrefixUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import play.Logger;
import play.exceptions.ActionNotFoundException;
import play.mvc.ActionInvoker;
import play.mvc.Router;
import play.mvc.Http.Request;
import play.mvc.Router.ActionDefinition;
import play.utils.Java;

/**
 * TODO DOCUMENT ME
 * 
 */

public class PlayActionAttributeModifierAttrProcessor extends AbstractSingleAttributeModifierAttrProcessor {
    private static final Pattern PARAM_PATTERN = Pattern.compile("^\\s*.*?\\((.*)\\)\\s*$");

    public static final Integer ATTR_PRECEDENCE = Integer.valueOf(1000);
    
    public static final String[] ATTR_NAMES = 
        new String[] {
                "action",
                "href",
                "name",
                "src",
                "type",
        };

    public Set<AttrApplicability> getAttributeApplicabilities() {
        return AttrApplicability.createSetForAttrNames(ATTR_NAMES);
    }

    public Integer getPrecedence() {
        return ATTR_PRECEDENCE;
    }


    @Override
    protected String getTargetAttributeName(
            final Arguments arguments, final TemplateResolution templateResolution, final Document document, 
            final Element element, final Attr attribute, final String attributeName, final String attributeValue) {
        return PrefixUtils.getUnprefixed(attributeName);
    }

    
    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, 
            final Attr attribute, 
            final String attributeName, final String attributeValue,
            final String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }



    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final TemplateResolution templateResolution, 
            final Document document, final Element element, 
            final Attr attribute, 
            final String attributeName, final String attributeValue,
            final String newAttributeName) {
        return false;
    }

    @Override
    protected String getTargetAttributeValue(
            final Arguments arguments, final TemplateResolution templateResolution,
            final Document document, final Element element, final Attr attribute,
            final String attributeName, final String attributeValue) {
        
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

        Object obj = OgnlExpressionEvaluator.INSTANCE.evaluate(arguments, templateResolution, exp, arguments.getExpressionEvaluationRoot());
        if (obj instanceof Map) {
            return Router.reverse(attributeValue, (Map) obj)
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

        if (paramNames.length > list.size()) {
            Logger.warn("param length unmatched. %s", Arrays.toString(paramNames));
            throw new ActionNotFoundException(attributeValue, null);
        }

        for (int i = 0; i < paramNames.length; i++) {
            paramMap.put(paramNames[i], list.get(i));
        }

        return Router.reverse(extracted, paramMap)
                     .toString();
    }
}
