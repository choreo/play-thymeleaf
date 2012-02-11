package thymeleaf.dialect;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.exceptions.AttrProcessorException;
import org.thymeleaf.processor.applicability.AttrApplicability;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;
import org.thymeleaf.standard.expression.Assignation;
import org.thymeleaf.standard.expression.AssignationSequence;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;
import org.thymeleaf.templateresolver.TemplateResolution;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import play.Logger;
import play.Play;
import play.exceptions.ActionNotFoundException;
import play.mvc.ActionInvoker;
import play.mvc.Http.Request;
import play.mvc.Router;
import play.mvc.Router.ActionDefinition;
import play.utils.Java;
import play.vfs.VirtualFile;

/**
 * TODO DOCUMENT ME
 */

public class PlayActionAttrProcessor extends AbstractAttributeModifierAttrProcessor {
    private static final Pattern PARAM_PATTERN = Pattern.compile("^\\s*.*?\\((.+)\\)\\s*$");

    @Override
    public Set<AttrApplicability> getAttributeApplicabilities() {
        return AttrApplicability.createSetForAttrName("attr");
    }

    @Override
    public Integer getPrecedence() {
        return 10000;
    }

    @Override
    protected Map<String, String> getNewAttributeValues(Arguments arguments,
                    TemplateResolution templateResolution, Document document, Element element,
                    Attr attribute, String attributeName, String attributeValue) {

        final AssignationSequence assignations = StandardExpressionProcessor.parseAssignationSequence(
                        arguments, templateResolution, attributeValue);
        if (assignations == null) {
            throw new AttrProcessorException("Could not parse value as attribute assignations: \""
                            + attributeValue + "\"");
        }

        final Map<String, String> newAttributeValues = new LinkedHashMap<String, String>();

        for (final Assignation assignation : assignations) {

            final String newAttributeName = assignation.getLeft()
                                                       .getValue();
            final Expression expression = assignation.getRight();
            String action = StringUtils.strip(expression.getStringRepresentation(), "'");
            String parsedAction = action;
            if (action.startsWith("$")) {
                Expression exp = StandardExpressionProcessor.parseExpression(arguments,
                                templateResolution, action);
                final Object x = StandardExpressionProcessor.executeExpression(arguments,
                                templateResolution, exp);
                parsedAction = ObjectUtils.toString(x);
            }

            // extract parameter names and values
            Map<String, Object> map = new HashMap<String, Object>();
            Matcher matcher = PARAM_PATTERN.matcher(parsedAction);
            Object[] actionMethods = null;
            try {
                String extracted = StringUtils.substringBefore(parsedAction, "(");
                if (!extracted.contains(".")) {
                    extracted = Request.current().controller + "." + extracted;
                }
                actionMethods = ActionInvoker.getActionMethod(extracted);
            } catch (ActionNotFoundException e) {
                Logger.warn(e, "action conversion error. %s", assignation);
                continue;
            }

            if (matcher.matches()) {
                String[] paramNames = null;
                try {
                    paramNames = Java.parameterNames((Method) actionMethods[1]);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                String exp = matcher.group(1);
                String[] params = exp.split(",");
                if (params.length == paramNames.length) {
                    for (int i = 0; i < paramNames.length; i++) {
                        map.put(paramNames[i], params[i]);
                    }
                }
            }
            final ActionDefinition def = Router.reverse("Application.sayHello", map);
            newAttributeValues.put(newAttributeName, (def.toString()));

        }

        return Collections.unmodifiableMap(newAttributeValues);
    }

    @Override
    protected ModificationType getModificationType(Arguments arguments,
                    TemplateResolution templateResolution, Document document, Element element,
                    Attr attribute, String attributeName, String attributeValue,
                    String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }

    @Override
    protected boolean removeAttributeIfEmpty(Arguments arguments,
                    TemplateResolution templateResolution, Document document, Element element,
                    Attr attribute, String attributeName, String attributeValue,
                    String newAttributeName) {
        return false;
    }

}
