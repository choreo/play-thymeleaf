package play.modules.thymeleaf.templates;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;
import play.Logger;
import play.i18n.Lang;
import play.modules.thymeleaf.ThymeleafPlayException;
import play.templates.Template;
import play.vfs.VirtualFile;

/**
 * TODO DOCUMENT ME
 */

public class ThymeleafTemplate extends Template {
    private TemplateEngine templateEngine;

    private VirtualFile file;
    
    public ThymeleafTemplate(TemplateEngine templateEngine, VirtualFile file) {
        this.templateEngine = templateEngine;
        this.file = file;
    }

    @Override
    public void compile() {
        Logger.trace("nothing to do");
    }
    
    @Override
    protected String internalRender(Map<String, Object> args) {
        // the results of Lang.getLocale() and new Locale(Lang.get()) are not the same for the default locale.
        // Groovy template extension uses the latter.
        Context context = new PlayContext(new Locale(Lang.get()), args);
        try {
            if (Logger.isTraceEnabled()) Logger.trace("args = %s", args);
            return templateEngine.process(this.name, context);
        } catch (TemplateProcessingException e) {
            throw new ThymeleafPlayException(this, e);
        }
    }
}
