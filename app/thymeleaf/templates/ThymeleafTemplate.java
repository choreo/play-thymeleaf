package thymeleaf.templates;

import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.OutputCreationException;
import org.thymeleaf.exceptions.TemplateProcessingException;

import play.Logger;
import play.Play;
import play.i18n.Lang;
import play.templates.Template;
import thymeleaf.ThymeleafPlayException;

/**
 * TODO DOCUMENT ME
 */

public class ThymeleafTemplate extends Template {
    private TemplateEngine templateEngine;

    public ThymeleafTemplate(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public void compile() {
        Logger.trace("nothing to do");
    }

    @Override
    protected String internalRender(Map<String, Object> args) {
        try {
            Context context = new PlayContext(Lang.getLocale(), args);
            if (Logger.isTraceEnabled()) Logger.trace("args = %s", args);
            return templateEngine.process(this.name, context);
        } catch (TemplateProcessingException e) {
            //throw e;
            throw new ThymeleafPlayException(this, e);
        }
    }

}
