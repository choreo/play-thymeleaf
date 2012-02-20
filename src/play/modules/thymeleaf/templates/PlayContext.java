package play.modules.thymeleaf.templates;

import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.thymeleaf.context.Context;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.VariablesMap;
import org.thymeleaf.standard.expression.LinkExpression;

import play.modules.thymeleaf.servlet.MockHttpServletRequest;


/**
 * TODO DOCUMENT ME {@link LinkExpression}で使えるように。
 */

public class PlayContext extends Context implements IWebContext {
    private MockHttpServletRequest request = new MockHttpServletRequest();

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
}
