package play.modules.thymeleaf.dialect;

import java.util.HashMap;
import java.util.Map;

import ognl.DefaultClassResolver;
import play.Play;

/**
 * This resolver searches a class from Play's ApplicationClassLoader if DefaultClassResolver fails to load it. 
 */

public class PlayClassResolver extends DefaultClassResolver {
    private Map classes = new HashMap(101);

    @Override
    public Class classForName(String className, Map context) throws ClassNotFoundException {
        Class result = null;

        if ((result = (Class) classes.get(className)) == null) {
            try {
                result = Class.forName(className);
            } catch (ClassNotFoundException ex) {
                if (className.indexOf('.') == -1) {
                    result = Class.forName("java.lang." + className);
                    classes.put("java.lang." + className, result);
                }
            }

            if (result == null) {
                result = Class.forName(className, true, Play.classloader);
            }

            classes.put(className, result);
        }

        return result;
    }
    
    void clearClassCache() {
        this.classes.clear();
    }
}
