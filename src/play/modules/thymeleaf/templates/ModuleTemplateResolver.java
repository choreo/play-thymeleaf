package play.modules.thymeleaf.templates;

import java.io.File;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.FileResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import play.Play;
import play.vfs.VirtualFile;

/**
 * Optional template resolver class that loads template files from module.
 * You can add this resolver within the 'OnApplicationStart' job.
 * <pre>
 *       TemplateEngine engine = Play.plugin(ThymeleafPlugin.class)
 *                                   .getTemplateEngine();
 *       engine.addTemplateResolver(new ModuleTemplateResolver("modulename"));
 * </pre>
 */

public class ModuleTemplateResolver extends TemplateResolver {
    private String moduleName;

    /**
     * @param moduleName
     *            your module name which contains thymeleaf template files
     */
    public ModuleTemplateResolver(String moduleName) {
        super();
        this.moduleName = moduleName;
        setResourceResolver(new FileResourceResolver());
        setPrefix(Play.configuration.getProperty("thymeleaf.prefix", "/app/thviews"));
    }

    @Override
    protected String computeResourceName(TemplateProcessingParameters templateProcessingParameters) {
        VirtualFile moduleRoot = Play.modules.get(this.moduleName);
        return new File(moduleRoot.getRealFile(), super.computeResourceName(templateProcessingParameters)).getPath();
    }

}
