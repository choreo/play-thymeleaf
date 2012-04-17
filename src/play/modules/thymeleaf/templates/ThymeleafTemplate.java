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

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateProcessingException;
import play.Logger;
import play.i18n.Lang;
import play.modules.thymeleaf.ThymeleafPlayException;
import play.modules.thymeleaf.adapter.FlashAdapter;
import play.modules.thymeleaf.adapter.ParamsAdapter;
import play.templates.JavaExtensions;
import play.templates.Template;
import play.vfs.VirtualFile;

/**
 * Plugin template class.
 */

public class ThymeleafTemplate extends Template {
    private static final String EXT_EVALUATION_VARIABLE_NAME = "ext";
    
    private static final JavaExtensions EXTENSIONS = new JavaExtensions();
    
    private TemplateEngine templateEngine;

    @SuppressWarnings("unused")
    private VirtualFile file;
    
    /**
     * @param templateEngine
     * @param file
     */
    public ThymeleafTemplate(TemplateEngine templateEngine, VirtualFile file) {
        this.templateEngine = templateEngine;
        this.file = file;
    }

    @Override
    public void compile() {
        Logger.trace("nothing to do");
    }
    
    /**
     * Calls TemplateEngine#process(String, org.thymeleaf.context.IContext).
     * Also wraps "flash" and "params" variable objects by Map implementation for easy access by OGNL.
     */
    @Override
    protected String internalRender(Map<String, Object> args) {
        if (!args.containsKey(EXT_EVALUATION_VARIABLE_NAME)) {
            args.put(EXT_EVALUATION_VARIABLE_NAME, EXTENSIONS);
        }
        
        args.put("flash", new FlashAdapter());
        args.put("params", new ParamsAdapter());
        
        // the results of Lang.getLocale() and new Locale(Lang.get()) are not the same for the default locale.
        // Groovy template extension uses the latter.
        String lng = Lang.get();
        Locale locale = StringUtils.isEmpty(lng) ? Locale.getDefault() : new Locale(Lang.get());
        Context context = new PlayContext(locale, args);
        try {
            if (Logger.isTraceEnabled()) Logger.trace("args = %s", args);
            return templateEngine.process(this.name, context);
        } catch (TemplateProcessingException e) {
            throw new ThymeleafPlayException(this, e);
        }
    }
}
