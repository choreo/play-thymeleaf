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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.context.Context;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.VariablesMap;
import play.modules.thymeleaf.servlet.MockHttpServletRequest;


/**
 * Context implementation for playframework.  This class impmlements IWebContexxt so thymeleaf LinkExpression can be available.
 */
public class PlayContext extends Context implements IWebContext {
    private MockHttpServletRequest request = new MockHttpServletRequest();

    /**
     * @param locale
     * @param args
     */
    public PlayContext(Locale locale, Map<String, Object> args) {
        super(locale, args);
    }

    @Override
    public HttpServletRequest getHttpServletRequest() {
        return this.request;
    }

    @Override
    public HttpSession getHttpSession() {
        return request.getSession();
    }

    @Override
    public ServletContext getServletContext() {
        // No Impl
        return null;
    }

    @Override
    public VariablesMap<String, String[]> getRequestParameters() {
        return new VariablesMap<String, String[]>();
    }

    @Override
    public VariablesMap<String, Object> getRequestAttributes() {
        return new VariablesMap<String, Object>();
    }

    @Override
    public VariablesMap<String, Object> getSessionAttributes() {
        return new VariablesMap<String, Object>();
    }

    @Override
    public VariablesMap<String, Object> getApplicationAttributes() {
        return new VariablesMap<String, Object>();
    }

    @Override
    public HttpServletResponse getHttpServletResponse() {
        // No Impl
        return null;
    }
}
