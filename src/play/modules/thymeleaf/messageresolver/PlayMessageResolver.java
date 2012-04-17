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
package play.modules.thymeleaf.messageresolver;

import org.thymeleaf.Arguments;
import org.thymeleaf.messageresolver.AbstractMessageResolver;
import org.thymeleaf.messageresolver.MessageResolution;

import play.i18n.Lang;
import play.i18n.Messages;

/**
 * An IMessageResolver implementation class that delegates a resolution to
 * play.i18n.Messages.
 */

public class PlayMessageResolver extends AbstractMessageResolver {
    /**
     * Constructs a resolver of order 100.
     */
    public PlayMessageResolver() {
        setOrder(100);
    }
    @Override
    public MessageResolution resolveMessage(
                    final Arguments arguments, final String key, final Object[] messageParameters) {

        checkInitialized();

        final String message = Messages.getMessage(Lang.get(), key, messageParameters);
        return new MessageResolution(message);
    }
    

}
