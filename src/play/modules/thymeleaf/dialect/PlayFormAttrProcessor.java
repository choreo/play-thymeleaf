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
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Attribute;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;

import play.mvc.Http;
import play.mvc.Scope.Session;

/**
 * "form" Attribute Processor.  it acts like #{form /} tag in Groovy template.
 * 
 */

public class PlayFormAttrProcessor extends AbstractAttributeModifierAttrProcessor {
    /** attribute precedence */
    public static final int ATTR_PRECEDENCE = 1001;
    /** processor name */
    public static final String ATTR_NAME = "form";

    /**
     * 
     */
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


    /**
     * Appends other attributes like Groovy template #{form /} tag.
     * This is the sustitute for FastTags#_form(Map, groovy.lang.Closure, java.io.PrintWriter, play.templates.GroovyTemplate.ExecutableTemplate, int).
     */
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
