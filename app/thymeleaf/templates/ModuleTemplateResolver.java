package thymeleaf.templates;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import play.vfs.VirtualFile;

/**
 * TODO DOCUMENT ME
 */

public class ModuleTemplateResolver extends TemplateResolver {
    private String moduleName;

    public ModuleTemplateResolver(VirtualFile module) {
        super();
        this.moduleName = module.getName();
        super.setResourceResolver(new PlayFileResourceResolver());

        setPrefix(module.getRealFile()
                    .getAbsolutePath());

        Set<String> resolvablePatterns = new HashSet<String>();
        resolvablePatterns.add("\\{module\\:" + this.moduleName + "\\}*");
        setResolvablePatterns(resolvablePatterns);

    }

    @Override
    protected String computeResourceName(final TemplateProcessingParameters templateProcessingParameters) {

        checkInitialized();

        String computed = unsafeGetPrefix()
                        + StringUtils.removeStart(templateProcessingParameters.getTemplateName(), "{module:"
                                        + this.moduleName + "}");
        return computed;
    }

    /**
     * <p>
     * This method <b>should not be called</b>, because the resource resolver is
     * fixed to be {@link PlayTemplateResolver}. Every execution of this
     * method will result in an exception.
     * </p>
     * <p>
     * If you need to select a different resource resolver, use the
     * {@link TemplateResolver} class instead.
     * </p>
     * 
     * @param resourceResolver
     *            the new resource resolver
     */
    @Override
    public synchronized void setResourceResolver(final IResourceResolver resourceResolver) {
        throw new ConfigurationException("Cannot set a resource resolver on " + this.getClass()
                                                                                    .getName()
                        + ". If " + "you want to set your own resource resolver, use "
                        + TemplateResolver.class.getName() + " instead");
    }

}
