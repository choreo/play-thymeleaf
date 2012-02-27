package play.modules.thymeleaf;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.thymeleaf.exceptions.TemplateProcessingException;
import play.exceptions.TemplateException;
import play.modules.thymeleaf.templates.ThymeleafTemplate;

/**
 * TemplateException which describes thymeleaf template errors.
 */
public class ThymeleafPlayException extends TemplateException {
    private static final long serialVersionUID = 1L;

    TemplateProcessingException templateEx;

    /**
     * @param template
     * @param e
     *            Thymeleaf specific exception
     */
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
                                                   .getSimpleName(), StringEscapeUtils.escapeHtml(getMessage()));
    }

    @Override
    public boolean isSourceAvailable() {
        return false;
    }
}
