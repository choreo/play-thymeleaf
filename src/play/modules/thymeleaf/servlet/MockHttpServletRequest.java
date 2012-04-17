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
package play.modules.thymeleaf.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.IteratorUtils;

import play.Play;
import play.i18n.Lang;
import play.mvc.Http.Request;
import play.mvc.Scope.Session;

/**
 * Mock HttpServletRequest class.  Internal use for LinkExpresion.
 */
public class MockHttpServletRequest implements HttpServletRequest {
    private HttpSession session = new MockHttpSession();

    @Override
    public Object getAttribute(String arg0) {
        // No impl
        return null;
    }

    @Override
    public Enumeration<?> getAttributeNames() {
        // No impl
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        // No impl
        return null;
    }

    @Override
    public int getContentLength() {
        // No impl
        return 0;
    }

    @Override
    public String getContentType() {
        // No impl
        return null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // No impl
        return null;
    }

    @Override
    public String getLocalAddr() {
        // No impl
        return null;
    }

    @Override
    public String getLocalName() {
        // No impl
        return null;
    }

    @Override
    public int getLocalPort() {
        // No impl
        return 0;
    }

    @Override
    public Locale getLocale() {
        return Lang.getLocale();
    }

    @Override
    public Enumeration<?> getLocales() {
        List<String> list = Request.current()
                                   .acceptLanguage();
        List<Locale> locales = new ArrayList<Locale>(list.size());
        for (String lang : list) {
            locales.add(new Locale(lang));
        }
        return IteratorUtils.asEnumeration(locales.iterator());
    }

    @Override
    public String getParameter(String arg0) {
        // No impl
        return null;
    }

    @Override
    public Map<?, ?> getParameterMap() {
        // No impl
        return null;
    }

    @Override
    public Enumeration<?> getParameterNames() {
        // No impl
        return null;
    }

    @Override
    public String[] getParameterValues(String arg0) {
        // No impl
        return null;
    }

    @Override
    public String getProtocol() {
        // No impl
        return null;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        // No impl
        return null;
    }

    @Override
    public String getRealPath(String arg0) {
        // No impl
        return null;
    }

    @Override
    public String getRemoteAddr() {
        // No impl
        return null;
    }

    @Override
    public String getRemoteHost() {
        // No impl
        return null;
    }

    @Override
    public int getRemotePort() {
        // No impl
        return 0;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String arg0) {
        // No impl
        return null;
    }

    @Override
    public String getScheme() {
        // No impl
        return null;
    }

    @Override
    public String getServerName() {
        // No impl
        return null;
    }

    @Override
    public int getServerPort() {
        // No impl
        return 0;
    }

    @Override
    public boolean isSecure() {
        // No impl
        return false;
    }

    @Override
    public void removeAttribute(String arg0) {
        // No impl

    }

    @Override
    public void setAttribute(String arg0, Object arg1) {
        // No impl

    }

    @Override
    public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {
        // No impl

    }

    @Override
    public String getAuthType() {
        // No impl
        return null;
    }

    @Override
    public String getContextPath() {
        return Play.ctxPath;
    }

    @Override
    public Cookie[] getCookies() {
        // No impl
        return null;
    }

    @Override
    public long getDateHeader(String arg0) {
        // No impl
        return 0;
    }

    @Override
    public String getHeader(String arg0) {
        // No impl
        return null;
    }

    @Override
    public Enumeration<?> getHeaderNames() {
        // No impl
        return null;
    }

    @Override
    public Enumeration<?> getHeaders(String arg0) {
        // No impl
        return null;
    }

    @Override
    public int getIntHeader(String arg0) {
        // No impl
        return 0;
    }

    @Override
    public String getMethod() {
        // No impl
        return null;
    }

    @Override
    public String getPathInfo() {
        // No impl
        return null;
    }

    @Override
    public String getPathTranslated() {
        // No impl
        return null;
    }

    @Override
    public String getQueryString() {
        // No impl
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return Request.current().url;
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(Request.current().url);
    }

    @Override
    public String getRequestedSessionId() {
        return Session.current()
                      .getId();
    }

    @Override
    public String getServletPath() {
        // No impl
        return null;
    }

    @Override
    public HttpSession getSession() {
        return this.session;
    }

    @Override
    public HttpSession getSession(boolean arg0) {
        return this.session;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    /**
     * @return true
     */
    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return true;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isUserInRole(String arg0) {
        return false;
    }

}
