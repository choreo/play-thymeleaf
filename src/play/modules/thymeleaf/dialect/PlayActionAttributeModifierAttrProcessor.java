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

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractSingleAttributeModifierAttrProcessor;
import org.thymeleaf.util.PrefixUtils;

/**
 * Attribute processor that handles play action expressions.
 * 
 */

public class PlayActionAttributeModifierAttrProcessor extends AbstractSingleAttributeModifierAttrProcessor {
    /** attribute precedence */
    public static final Integer ATTR_PRECEDENCE = Integer.valueOf(1000);
    
    /**
     * Attribute names this processor can handle.
     */
    public static final String[] ATTR_NAMES = 
                    new String[] {
                            "action",
                            "href",
                            "name",
                            "src",
                            "type",
                            "value"
                    };

    /** actual processor instances for each attribute name */
    public static final PlayActionAttributeModifierAttrProcessor[] PROCESSORS;
    
    static {
        
        PROCESSORS = new PlayActionAttributeModifierAttrProcessor[ATTR_NAMES.length];
        for (int i = 0; i < PROCESSORS.length; i++) {
            PROCESSORS[i] = new PlayActionAttributeModifierAttrProcessor(ATTR_NAMES[i]);
        }
        
    }

    private PlayActionAttributeModifierAttrProcessor(final String attributeName) {
        super(attributeName);
    }

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }


    @Override
    protected String getTargetAttributeName(
            final Arguments arguments, final Element element, final String attributeName) {
        return PrefixUtils.getUnprefixed(attributeName);
    }

    
    @Override
    protected ModificationType getModificationType(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return ModificationType.SUBSTITUTION;
    }
    
    @Override
    protected boolean removeAttributeIfEmpty(
            final Arguments arguments, final Element element, final String attributeName, final String newAttributeName) {
        return false;
    }

    @Override
    protected boolean recomputeProcessorsAfterExecution(final Arguments arguments,
            final Element element, final String attributeName) {
        return false;
    }

    /**
     * Parses the value using
     * {@link ProcessorUtil#toActionString(Arguments, String)}
     */
    @Override
    protected String getTargetAttributeValue(
            final Arguments arguments, final Element element, final String attributeName) {
        
        final String attributeValue = element.getAttributeValue(attributeName);
        return ProcessorUtil.toActionString(arguments, attributeValue);
    }
}
