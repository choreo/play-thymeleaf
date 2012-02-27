package play.modules.thymeleaf.servlet;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import play.mvc.Scope.Session;

/**
 * Mock HttpSession class.  Internal use for LinkExpresion.
 */

@SuppressWarnings("deprecation")
public class MockHttpSession implements HttpSession {

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
    public long getCreationTime() {
        // No impl
        return 0;
    }

    @Override
    public String getId() {
        return Session.current()
                      .getId();
    }

    @Override
    public long getLastAccessedTime() {
        // No impl
        return 0;
    }

    @Override
    public int getMaxInactiveInterval() {
        // No impl
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        // No impl
        return null;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        // No impl
        return null;
    }

    @Override
    public Object getValue(String arg0) {
        // No impl
        return null;
    }

    @Override
    public String[] getValueNames() {
        // No impl
        return null;
    }

    @Override
    public void invalidate() {
        // No impl

    }

    @Override
    public boolean isNew() {
        // No impl
        return false;
    }

    @Override
    public void putValue(String arg0, Object arg1) {
        // No impl

    }

    @Override
    public void removeAttribute(String arg0) {
        // No impl

    }

    @Override
    public void removeValue(String arg0) {
        // No impl

    }

    @Override
    public void setAttribute(String arg0, Object arg1) {
        // No impl

    }

    @Override
    public void setMaxInactiveInterval(int arg0) {
        // No impl

    }

}
