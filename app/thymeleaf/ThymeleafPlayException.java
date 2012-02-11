package thymeleaf;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.thymeleaf.exceptions.TemplateProcessingException;

import play.exceptions.PlayException;
import play.exceptions.TemplateException;
import play.templates.Template;

/**
 * TODO DOCUMENT ME
 */

public class ThymeleafPlayException extends TemplateException {
    TemplateProcessingException templateEx;

    public ThymeleafPlayException(Template template, TemplateProcessingException e) {
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

}
