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
package play.modules.thymeleaf;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;

import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.modules.thymeleaf.dialect.PlayDialect;
import play.modules.thymeleaf.templates.PlayTemplateResolver;
import play.modules.thymeleaf.templates.ThymeleafTemplate;
import play.templates.Template;
import play.vfs.VirtualFile;

/**
 * The plugin class for thmeleaf template engine. The following configuration
 * options are available. You can override them in application.conf.
 * <ul>
 * <li>thymeleaf.prefix - Prefix to prepend used by the default template resolver.
 * Default:/app/thviews</li>
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
        this.prefix = Play.configuration.getProperty("thymeleaf.prefix", "/app/thviews");
        this.suffix = Play.configuration.getProperty("thymeleaf.suffix");
        this.mode = Play.configuration.getProperty("thymeleaf.templatemode",  StandardTemplateModeHandlers.XHTML.getTemplateModeName());
        this.ttlString = Play.configuration.getProperty("thymeleaf.cache.ttl");
    }
    
    @Override
    public void onApplicationStart() {
        
        PlayTemplateResolver playResolver = new PlayTemplateResolver();
        VirtualFile throot = VirtualFile.open(Play.applicationPath).child(this.prefix);
        playResolver.setPrefix(this.prefix);
        // add this path on top in order to search plugin template first
        Play.templatesPath.add(0, throot);

        if (this.suffix != null) {
            playResolver.setSuffix(this.suffix);
        }

        playResolver.setTemplateMode(this.mode);

        switch (Play.mode) {
        case DEV:
            playResolver.setCacheable(false);
            break;
        default:
            if (ttlString != null) {
                if (!NumberUtils.isDigits(ttlString)) {
                    Logger.warn("Configuration 'thymeleaf.cache.ttl' value %s must be number(in millisecond).", ttlString);
                    ttlString = "0";
                }
                playResolver.setCacheTTLMs(Long.valueOf(ttlString));
            }
            break;
        }
        
        templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(playResolver);
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
     * Loads template from thymeleaf TemplateEngine if the file is in the prefix path.
     */
    @Override
    public Template loadTemplate(VirtualFile file) {
        String relativePath = file.relativePath();
        String templatePath = StringUtils.removeStart(relativePath, this.prefix);
        if(relativePath.length() == templatePath.length()) {
            if(Logger.isDebugEnabled()) Logger.debug("%s is not in thymeleaf path", templatePath);
            return null;
        }
        
        if (Logger.isDebugEnabled()) Logger.debug("loading %s from thymeleaf path %s", templatePath, this.prefix);

        Template template = new ThymeleafTemplate(templateEngine, file);
        template.name = templatePath;
        return template;
    }
}
