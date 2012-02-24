package play.modules.thymeleaf.dialect;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;

import play.mvc.Http;
import play.mvc.Scope.Session;

/**
 * TODO DOCUMENT ME
 * 
 */

public class PlayFormAttrProcessor extends AbstractAttributeModifierAttrProcessor {
    public static final int ATTR_PRECEDENCE = 1001;
    public static final String ATTR_NAME = "form";

    
    
    public PlayFormAttrProcessor() {
        super(ATTR_NAME);
    }
    


    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }
    
    
    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }



    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return true;
    }



    @Override
    protected final Map<String,String> getModifiedAttributeValues(
            final Arguments arguments, final Element element, final String attributeName) {


        if (!"form".equalsIgnoreCase(element.getOriginalName())) {
            return Collections.emptyMap();
        }
        
        Map<String, Attribute> attrMap = element.getAttributeMap();
        
        final Map<String,String> newAttributeValues = new HashMap<String,String>();
        
        final String attributeValue = element.getAttributeValue(attributeName);
        
        String action = ProcessorUtil.toActionString(arguments, attributeValue);
        newAttributeValues.put("action", action);

        if (!attrMap.containsKey("enctype")) {
            newAttributeValues.put("enctype", "application/x-www-form-urlencoded");
        }        
        
        if (!attrMap.containsKey("method")) {
            newAttributeValues.put("method", "post");
        }        
        
        String method = attrMap.containsKey("method") ? attrMap.get("method").getValue() : newAttributeValues.get("method");
        
        if (!("get".equalsIgnoreCase(method) || "post".equalsIgnoreCase(method))) {
            String separator = action.indexOf('?') != -1 ? "&" : "?";
            action += separator + "x-http-method-override=" + method.toUpperCase();
            newAttributeValues.put("action", action);
            newAttributeValues.put("method", "post");
        }

        String encoding = Http.Response.current().encoding;
        newAttributeValues.put("accept-charset", encoding);
        
        
        Element authTokenHidden = new Element("input");
        authTokenHidden.setAttribute("type", "hidden");
        authTokenHidden.setAttribute("name", "authenticityToken");
        authTokenHidden.setAttribute("value", Session.current().getAuthenticityToken());
        element.insertChild(0, authTokenHidden);
        element.setRecomputeProcessorsImmediately(true);
        
        return Collections.unmodifiableMap(newAttributeValues);
    }



    @Override
    protected boolean recomputeProcessorsAfterExecution(final Arguments arguments,
            final Element element, final String attributeName) {
        return false;
    }

}
