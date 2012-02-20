package play.modules.thymeleaf.templates;

import java.util.Set;

import org.thymeleaf.Arguments;
import org.thymeleaf.exceptions.ConfigurationException;
import org.thymeleaf.resourceresolver.FileResourceResolver;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import play.Play;

/**
 * TODO DOCUMENT ME
 */

public class PlayTemplateResolver extends TemplateResolver {
    public PlayTemplateResolver() {
        super();
        super.setResourceResolver(new PlayFileResourceResolver());
    }

    /**
     * <p>
     *   This method <b>should not be called</b>, because the resource resolver is
     *   fixed to be {@link PlayFileResourceResolver}. Every execution of this method
     *   will result in an exception.
     * </p>
     * <p>
     *   If you need to select a different resource resolver, use the {@link TemplateResolver}
     *   class instead.
     * </p>
     * 
     * @param resourceResolver the new resource resolver
     */
    @Override
    public synchronized void setResourceResolver(final IResourceResolver resourceResolver) {
        throw new ConfigurationException(
                "Cannot set a resource resolver on " + this.getClass().getName() + ". If " +
                "you want to set your own resource resolver, use " + TemplateResolver.class.getName() + 
                "instead");
    }

}
