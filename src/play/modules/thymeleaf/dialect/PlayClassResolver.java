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
