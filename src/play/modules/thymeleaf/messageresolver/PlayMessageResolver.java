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
