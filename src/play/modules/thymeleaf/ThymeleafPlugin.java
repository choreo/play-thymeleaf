package play.modules.thymeleaf;

import java.lang.reflect.Method;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.thymeleaf.TemplateEngine;
import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.modules.thymeleaf.dialect.PlayDialect;
import play.modules.thymeleaf.templates.ModuleTemplateResolver;
import play.modules.thymeleaf.templates.PlayTemplateResolver;
import play.modules.thymeleaf.templates.ThymeleafTemplate;
import play.mvc.ActionInvoker;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.templates.Template;
import play.vfs.VirtualFile;

/**
 * The plugin class for thmeleaf template engine. The following configuration
 * options are available. You can override them in application.conf.
 * <ul>
 * <li>thymeleaf.prefix - Prefix to append to the default template resolver.
 * Default:$APP_PATH/app/thviews</li>
 * <li>thymeleaf.suffix - Template suffix. Default: null</li>
 * <li>thymeleaf.templatemode - Thymeleaf template mode. Default: XHTML</li>
 * <li>thymeleaf.cache.ttl - template chache ttl in milliseconds for production mode.  Default: thymeleaf default </li>
 * <li>thymeleaf.enhancer.enabled - whether to enhance the application classes in order to remove "synthetic" flags for OGNL.  Default: true</li>
 * </ul>
 */

public class ThymeleafPlugin extends PlayPlugin {
    private TemplateEngine templateEngine;

    private boolean enhancerEnabled;
    
    private String prefix;
    
    private String suffix;
    
    private String mode;
    
    private String ttlString;

    @Override
    public void onLoad() {
        this.enhancerEnabled = BooleanUtils.toBoolean(Play.configuration.getProperty("thymeleaf.enhancer.enabled", "true"));
        Logger.debug("thymeleaf plugin enhancer enabled ? :%s", enhancerEnabled);
    }
    
    @Override
    public void onConfigurationRead() {
        this.prefix = Play.configuration.getProperty("thymeleaf.prefix", Play.applicationPath.getAbsolutePath()) + "/app/thviews";
        this.suffix = Play.configuration.getProperty("thymeleaf.suffix");
        this.mode = Play.configuration.getProperty("thymeleaf.templatemode", "XHTML");
        this.ttlString = Play.configuration.getProperty("thymeleaf.cache.ttl");
    }
    
    @Override
    public void onApplicationStart() {
        
        PlayTemplateResolver playResolver = new PlayTemplateResolver();
        playResolver.setPrefix(this.prefix);

        if (this.suffix != null) {
            playResolver.setSuffix(this.suffix);
        }

        ModuleTemplateResolver moduleResolver = new ModuleTemplateResolver(Play.modules.get("thymeleaf"));

        playResolver.setTemplateMode(this.mode);
        moduleResolver.setTemplateMode("XHTML");

        switch (Play.mode) {
        case DEV:
            playResolver.setCacheable(false);
            moduleResolver.setCacheable(false);
            break;
        default:
            if (ttlString != null) {
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

    @Override
    public void enhance(ApplicationClass applicationClass) throws Exception {
        if (enhancerEnabled) new FixSyntheticEnhancer().enhanceThisClass(applicationClass);
    }
    
    /**
     * @return Returns the templateEngine.
     */
    public TemplateEngine getTemplateEngine() {
        return templateEngine;
    }

    /**
     * Loads template from thymeleaf TemplateEngine if the annotation {@link UseThymeleaf} is present.
     */
    @Override
    public Template loadTemplate(VirtualFile file) {
        if (Request.current() == null) {
            // probably precompiling...
            if (isErrorPage(file)) {
                Template template = new ThymeleafTemplate(templateEngine, file);
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

        Template template = new ThymeleafTemplate(templateEngine, file);
        String path = file.relativePath();
        // replace /app/views with thymeleaf prefix unless it is inside a module
        template.name = path.startsWith("{") ? path : path.substring(10);
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
