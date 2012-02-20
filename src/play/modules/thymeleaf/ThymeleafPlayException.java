package play.modules.thymeleaf;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.thymeleaf.exceptions.TemplateProcessingException;
import play.exceptions.TemplateException;
import play.modules.thymeleaf.templates.ThymeleafTemplate;

/**
 * TODO DOCUMENT ME
 */

public class ThymeleafPlayException extends TemplateException {
    TemplateProcessingException templateEx;

    public ThymeleafPlayException(ThymeleafTemplate template, TemplateProcessingException e) {
        super(template, e.getLineNumber(), e.getMessage(), e);
        templateEx = e;
    }

    @Override
    public String getErrorTitle() {
        return String.format("Template error %s", ExceptionUtils.getRootCauseMessage(templateEx));
    }

    @Override
    public String getErrorDescription() {
        return String.format(
                        "Execution error occured in template <strong>%s</strong>. Exception raised was <strong>%s</strong> : <strong>%s</strong>.",
                        getSourceFile(), getCause().getClass()
                                                   .getSimpleName(),
                        StringEscapeUtils.escapeHtml(getMessage()));
    }
    
    @Override
    public boolean isSourceAvailable() {
        return false;
    }
}
