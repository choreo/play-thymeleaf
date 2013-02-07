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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;
import org.thymeleaf.templateresolver.TemplateResolver;

import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.modules.thymeleaf.dialect.FixedStandardDialect;
import play.modules.thymeleaf.dialect.PlayDialect;
import play.modules.thymeleaf.dialect.PlayOgnlVariableExpressionEvaluator;
import play.modules.thymeleaf.templates.ModuleTemplateResolver;
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
    
    private List<String> thymeleafModules = new ArrayList<String>(1);

    @Override
    public void onLoad() {
        this.enhancerEnabled = BooleanUtils.toBoolean(Play.configuration.getProperty("thymeleaf.enhancer.enabled", "true"));
        Logger.debug("thymeleaf plugin enhancer enabled ? :%s", enhancerEnabled);
    }

    @Override
    public List<ApplicationClass> onClassesChange(List<ApplicationClass> modified) {
        PlayOgnlVariableExpressionEvaluator.INSTANCE.clearClassCache();
        return super.onClassesChange(modified);
    }
    
    @Override
    public void onConfigurationRead() {
        this.prefix = Play.configuration.getProperty("thymeleaf.prefix", "/app/thviews");
        this.suffix = Play.configuration.getProperty("thymeleaf.suffix");
        this.mode = Play.configuration.getProperty("thymeleaf.templatemode", StandardTemplateModeHandlers.XHTML.getTemplateModeName());
        this.ttlString = Play.configuration.getProperty("thymeleaf.cache.ttl");
        String[] modules = Play.configuration.getProperty("thymeleaf.modules", "")
                                             .trim()
                                             .split(",");
        for (String moduleName : modules) {
            if (!moduleName.isEmpty()) {
                this.thymeleafModules.add(moduleName.trim());
            }
        }
    }
    
    @Override
    public void onApplicationStart() {
        
        PlayTemplateResolver playResolver = new PlayTemplateResolver();
        this.prepareResolver(playResolver, VirtualFile.open(Play.applicationPath));
        
        List<ModuleTemplateResolver> moduleResolvers = new ArrayList<ModuleTemplateResolver>(this.thymeleafModules.size());
        for (String moduleName : this.thymeleafModules) {
            VirtualFile moduleRoot = Play.modules.get(moduleName);
            if (moduleRoot == null || !moduleRoot.exists()) {
                Logger.warn("module %s not found. Check the configuration value : %s", moduleName,
                                Play.configuration.getProperty("thymeleaf.modules"));
                continue;
            }
            
            ModuleTemplateResolver resolver = new ModuleTemplateResolver(moduleName);
            this.prepareResolver(resolver, moduleRoot);
            moduleResolvers.add(resolver);
            Logger.info("module %s registered for thymeleaf template search path", moduleName);
        }
        
        templateEngine = new TemplateEngine();
        templateEngine.addTemplateResolver(playResolver);
        
        for (ModuleTemplateResolver moduleTemplateResolver : moduleResolvers) {
            templateEngine.addTemplateResolver(moduleTemplateResolver);
        }
        
        templateEngine.setDialect(new FixedStandardDialect());
        templateEngine.addDialect(new PlayDialect());
    }
    
    private void prepareResolver(TemplateResolver templateResolver, VirtualFile rootDir) {
        VirtualFile throot = rootDir.child(this.prefix);
        templateResolver.setPrefix(this.prefix);
        // add this path on top in order to search plugin template first
        Play.templatesPath.add(0, throot);

        if (this.suffix != null) {
            templateResolver.setSuffix(this.suffix);
        }

        templateResolver.setTemplateMode(this.mode);

        switch (Play.mode) {
        case DEV:
            templateResolver.setCacheable(false);
            break;
        default:
            if (ttlString != null) {
                if (!NumberUtils.isDigits(ttlString)) {
                    Logger.warn("Configuration 'thymeleaf.cache.ttl' value %s must be number(in millisecond).", ttlString);
                    ttlString = "0";
                }
                templateResolver.setCacheTTLMs(Long.valueOf(ttlString));
            }
            break;
        }

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
        if (Logger.isDebugEnabled()) Logger.debug("relative path = %s", relativePath);
        String templatePath = null;

        if (StringUtils.startsWith(relativePath, this.prefix)) {

            templatePath = StringUtils.removeStart(relativePath, this.prefix);

        } else if (!this.thymeleafModules.isEmpty() && relativePath.startsWith("{module:")) {
            String moduleName = StringUtils.substringBetween(relativePath, "{module:", "}" + this.prefix);
            
            if (this.thymeleafModules.contains(moduleName)) {
                if (Logger.isDebugEnabled()) Logger.debug("module %s is thymeleaf enabled.", moduleName);
                templatePath = StringUtils.substringAfter(relativePath, "}" + this.prefix);
            }
        }

        if(templatePath == null) {
            if(Logger.isDebugEnabled()) Logger.debug("%s is not in thymeleaf path", relativePath);
            return null;
        }
        
        if (Logger.isDebugEnabled()) Logger.debug("loading %s from thymeleaf path %s", templatePath, this.prefix);

        Template template = new ThymeleafTemplate(templateEngine, file);
        template.name = templatePath;
        return template;
    }
}
