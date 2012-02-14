package thymeleaf.templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.Arguments;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.FileResourceResolver;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.util.Validate;

import play.Play;
import play.vfs.VirtualFile;

/**
 * TODO DOCUMENT ME
 */

public class PlayFileResourceResolver implements IResourceResolver {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String NAME = "PLAY_FILE";

    public PlayFileResourceResolver() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public InputStream getResourceAsStream(final TemplateProcessingParameters templateProcessingParameters, final String resourceName) {
        logger.debug("finding thymeleaf resource {} templatename = {}", resourceName,
                        templateProcessingParameters.getTemplateName());
        Validate.notNull(resourceName, "Resource name cannot be null");
        final File resourceFile = new File(resourceName);
        try {
            return new FileInputStream(resourceFile);
        } catch (FileNotFoundException e) {
            logger.debug("resource file not found {}", resourceName);
            return null;
        }
    }

}
