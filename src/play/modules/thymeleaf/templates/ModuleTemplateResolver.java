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
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.FileResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import play.Play;
import play.vfs.VirtualFile;

/**
 * Optional template resolver class that loads template files from module.
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
