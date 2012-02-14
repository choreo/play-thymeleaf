package thymeleaf;

import java.lang.reflect.Method;
import org.apache.commons.lang.math.NumberUtils;
import org.thymeleaf.TemplateEngine;
import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.mvc.ActionInvoker;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.templates.Template;
import play.vfs.VirtualFile;
import thymeleaf.dialect.PlayDialect;
import thymeleaf.templates.ModuleTemplateResolver;
import thymeleaf.templates.PlayTemplateResolver;
import thymeleaf.templates.ThymeleafTemplate;

/**
 * TODO DOCUMENT ME
 */

public class ThymeleafPlugin extends PlayPlugin {
    private TemplateEngine templateEngine;

    @Override
    public void onLoad() {
        Logger.info("ThymeleafPlugin.onLoad");

        PlayTemplateResolver playResolver = new PlayTemplateResolver();
        playResolver.setPrefix(Play.configuration.getProperty("thymeleaf.prefix", Play.applicationPath.getAbsolutePath()));

        if (Play.configuration.containsKey("thymeleaf.suffix")) {
            playResolver.setSuffix(Play.configuration.getProperty("thymeleaf.suffix"));
        }

        ModuleTemplateResolver moduleResolver = new ModuleTemplateResolver(Play.modules.get("thymeleaf"));

        String mode = Play.configuration.getProperty("thymeleaf.templatemode", "XHTML");
        playResolver.setTemplateMode(mode);
        moduleResolver.setTemplateMode("XHTML");

        switch (Play.mode) {
        case DEV:
            playResolver.setCacheable(false);
            moduleResolver.setCacheable(false);
            break;
        default:
            if (Play.configuration.containsKey("thymeleaf.cache.ttl")) {
                String ttlString = Play.configuration.getProperty("thymeleaf.cache.ttl");
                if (!NumberUtils.isDigits(ttlString)) {
                    Logger.warn("Configuration 'thymeleaf.cache.ttl' value %s must be number(in millisecond).", ttlString);
                    ttlString = "0";
                }
                playResolver.setCacheTTLMs(Long.valueOf(ttlString));
                moduleResolver.setCacheTTLMs(Long.valueOf(ttlString));
            }
            break;
        }
        
        templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(playResolver);
        templateEngine.addTemplateResolver(moduleResolver);
        templateEngine.addDialect(new PlayDialect());
    }

    /**
     * @return Returns the templateEngine.
     */
    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    @Override
    public Template loadTemplate(VirtualFile file) {
        if (Request.current() == null) {
            // probably precompiling...
            if (isErrorPage(file)) {
                Template template = new ThymeleafTemplate(templateEngine);
                template.name = file.relativePath();
                return template;
            }
            return null;
        }

        if (Request.current().controllerClass == null) {
            if (!isErrorPage(file)) {
                return null;
            }

        } else if (!isAnnotationPresent(Request.current().controllerClass)) {
            Method m = (Method) ActionInvoker.getActionMethod(Http.Request.current().action)[1];
            if (!m.isAnnotationPresent(UseThymeleaf.class)) {
                return null;
            }
        }

        Template template = new ThymeleafTemplate(templateEngine);
        template.name = file.relativePath();
        return template;
    }

    private boolean isErrorPage(VirtualFile file) {
        return file.getName()
                   .endsWith("404.html") || file.getName()
                                                .endsWith("500.html");
    }

    private boolean isAnnotationPresent(Class<? extends Controller> clazz) {
        Class<?> c = clazz;
        while (!c.equals(Object.class)) {
            if (c.isAnnotationPresent(UseThymeleaf.class)) {
                return true;
            }
            c = c.getSuperclass();
        }
        return false;
    }

}
