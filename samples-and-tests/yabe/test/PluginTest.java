import org.junit.*;

import enums.EnumTest;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.*;

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
}