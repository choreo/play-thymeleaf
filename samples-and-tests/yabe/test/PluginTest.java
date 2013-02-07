import org.junit.Test;

import play.mvc.Http.Response;
import play.test.FunctionalTest;
import enums.EnumTest;

public class PluginTest extends FunctionalTest {

    @Test
    public void testThatPageShouldBeRenderedWhenOnlyThymeleafPageExists() {
        Response response = GET("/Application/indexForTest");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset(play.Play.defaultWebEncoding, response);

        assertContentMatch("refer this value from thymeleaf template", response);
        assertContentMatch(EnumTest.TEST2.toString(), response);
    }

    @Test
    public void testThatModuleTemplateCanBeLoaded() {
        Response response = GET("/testmodule");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset(play.Play.defaultWebEncoding, response);

        assertContentMatch("test module file.", response);
    }
}
