/*
 * Copyright 2012 Satoshi Takata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package play.modules.thymeleaf.templates;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.util.Validate;

import play.Play;

/**
 * IResourceResolver implementation that returns FileInputStream.
 */

public class PlayFileResourceResolver implements IResourceResolver {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** ResourceResolver name */
    public static final String NAME = "PLAY_FILE";

    /**
     * 
     */
    public PlayFileResourceResolver() {
        super();
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Resolves resource and returns FileInputStream instance.
     */
    @Override
    public InputStream getResourceAsStream(final TemplateProcessingParameters templateProcessingParameters, final String resourceName) {
        if (logger.isDebugEnabled())
            logger.debug("locating thymeleaf resource {} templatename = {}", resourceName, templateProcessingParameters.getTemplateName());

        Validate.notNull(resourceName, "Resource name cannot be null");
        final File resourceFile = new File(Play.applicationPath, resourceName);
        try {
            return new FileInputStream(resourceFile);
        } catch (FileNotFoundException e) {
            logger.debug("resource file not found {}", resourceName);
            return null;
        }
    }

}
