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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

/**
 * A dialect class for Play!framework.
 */

public class PlayDialect extends AbstractDialect {

    @Override
    public String getPrefix() {
        return "pl";
    }

    /**
     * Non-lenient: if a tag or attribute with its prefix ('pl') appears on the
     * template and there is no valuetag/attribute processor * associated with
     * it, an exception is thrown.
     */
    @Override
    public boolean isLenient() {
        return false;
    }

    /**
     * Registers {@link PlayActionAttributeModifierAttrProcessor} and
     * {@link PlayFormAttrProcessor}.
     */
    @Override
    public Set<IProcessor> getProcessors() {
        final Set<IProcessor> attrProcessors = new HashSet<IProcessor>();
        attrProcessors.addAll(Arrays.asList(PlayActionAttributeModifierAttrProcessor.PROCESSORS));
        attrProcessors.add(new PlayFormAttrProcessor());
        attrProcessors.add(new PlayErrorAttrProcessor());
        
        return attrProcessors;
    }
}
