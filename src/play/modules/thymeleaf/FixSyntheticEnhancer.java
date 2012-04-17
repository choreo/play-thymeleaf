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
package play.modules.thymeleaf;

import java.lang.reflect.Modifier;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import play.Logger;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.classloading.enhancers.Enhancer;
import play.exceptions.UnexpectedException;

/**
 * Enhancer class that removes all synthetic flags from generated getter/setter
 * methods of application classes formerly added by PropertiesEnhancer.
 */

public class FixSyntheticEnhancer extends Enhancer {

    @Override
    public void enhanceThisClass(ApplicationClass applicationClass) throws Exception {
        final CtClass ctClass = makeClass(applicationClass);
        if (ctClass.isInterface()) {
            return;
        }
        if (ctClass.getName()
                   .endsWith(".package")) {
            return;
        }

        for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
            try {
                int mod = ctMethod.getModifiers();
                if (mod != (AccessFlag.PUBLIC | AccessFlag.SYNTHETIC)) {
                    // no need to be fixed
                    continue;
                }

                String methodName = ctMethod.getName();
                if (methodName.length() <= 3 || !(ctMethod.getName()
                                                          .startsWith("get") || ctMethod.getName()
                                                                                        .startsWith("set"))) {
                    // not a property
                    continue;
                }

                String propertyName = ctMethod.getName()
                                              .substring(3);
                propertyName = propertyName.substring(0, 1)
                                           .toLowerCase() + propertyName.substring(1);

                try {
                    CtField ctField = ctClass.getDeclaredField(propertyName);
                    if (!isProperty(ctField)) {
                        continue;
                    }
                } catch (NotFoundException e) {
                    // not a property, so just ignore it.
                    continue;
                }

                Logger.debug("removing synthetic flag from method %s#%s", ctClass.getName(), methodName);
                ctMethod.setModifiers(ctMethod.getModifiers() ^ AccessFlag.SYNTHETIC);

            } catch (Exception e) {
                Logger.error(e, "Error in FixSyntheticEnhancer");
                throw new UnexpectedException("Error in FixSyntheticEnhancer", e);
            }

        }

        // done
        applicationClass.enhancedByteCode = ctClass.toBytecode();
        ctClass.defrost();
    }

    /**
     * Is this field a valid javabean property ?
     */
    boolean isProperty(CtField ctField) {
        if (ctField.getName()
                   .equals(ctField.getName()
                                  .toUpperCase()) || ctField.getName()
                                                            .substring(0, 1)
                                                            .equals(ctField.getName()
                                                                           .substring(0, 1)
                                                                           .toUpperCase())) {
            return false;
        }
        return Modifier.isPublic(ctField.getModifiers()) && !Modifier.isFinal(ctField.getModifiers())
                        && !Modifier.isStatic(ctField.getModifiers());
    }

}
