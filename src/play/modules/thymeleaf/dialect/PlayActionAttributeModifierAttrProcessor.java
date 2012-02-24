package play.modules.thymeleaf.dialect;

import java.util.regex.Pattern;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractSingleAttributeModifierAttrProcessor;
import org.thymeleaf.util.PrefixUtils;

/**
 * TODO DOCUMENT ME
 * 
 */

public class PlayActionAttributeModifierAttrProcessor extends AbstractSingleAttributeModifierAttrProcessor {
    public static final Integer ATTR_PRECEDENCE = Integer.valueOf(1000);
    
    public static final String[] ATTR_NAMES = 
                    new String[] {
                            "action",
                            "href",
                            "name",
                            "src",
                            "type",
                            "value"
                    };

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

    @Override
    protected String getTargetAttributeValue(
            final Arguments arguments, final Element element, final String attributeName) {
        
        final String attributeValue = element.getAttributeValue(attributeName);
        return ProcessorUtil.toActionString(arguments, attributeValue);
    }
}
