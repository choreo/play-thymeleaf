import org.junit.*;
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
    }
}