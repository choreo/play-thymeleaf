package thymeleaf.dialect;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.attr.IAttrProcessor;

/**
 * TODO DOCUMENT ME
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

    @Override
    public Set<IAttrProcessor> getAttrProcessors() {
        final Set<IAttrProcessor> attrProcessors = new HashSet<IAttrProcessor>();
        attrProcessors.add(new PlayActionAttrProcessor());
        return attrProcessors;
    }
}
