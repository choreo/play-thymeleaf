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
        
        return attrProcessors;
    }
}
