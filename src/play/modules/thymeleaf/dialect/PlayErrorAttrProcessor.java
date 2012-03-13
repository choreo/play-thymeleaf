package play.modules.thymeleaf.dialect;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Text;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.attr.AbstractAttrProcessor;

import play.data.validation.Error;
import play.data.validation.Validation;

/**
 * "error" Attribute Processor. it acts like #{error /} tag in Groovy template.
 */
public class PlayErrorAttrProcessor extends AbstractAttrProcessor {
    /** attribute precedence */
    public static final int ATTR_PRECEDENCE = 1300;

    /** processor name */
    public static final String ATTR_NAME = "error";

    /**
     * 
     */
    public PlayErrorAttrProcessor() {
        super(ATTR_NAME);
    }

    @Override
    public int getPrecedence() {
        return ATTR_PRECEDENCE;
    }

    private String getText(final Arguments arguments, final Element element, final String attributeName) {
        final String attributeValue = element.getAttributeValue(attributeName);
        Error error = Validation.error(attributeValue);
        if (error != null) {
            return error.message();
        }
        return null;
    }

    @Override
    public ProcessorResult processAttribute(final Arguments arguments, final Element element, final String attributeName) {
        String msg = getText(arguments, element, attributeName);
        if (msg == null) {
            element.getParent()
                   .removeChild(element);
        } else {
            element.clearChildren();
            element.addChild(new Text(msg));
            element.removeAttribute(attributeName);
        }

        return ProcessorResult.OK;

    }

}
