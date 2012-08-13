package play.modules.thymeleaf.context;

import java.util.Map;

import ognl.OgnlContext;

import org.thymeleaf.context.IContext;
import org.thymeleaf.context.IProcessingContext;
import org.thymeleaf.context.ProcessingContext;

/**
 * TODO DOCUMENT ME
 */

public class PlayProcessingContext extends ProcessingContext {

    /**
     * @param context
     * @param localVariables
     * @param selectionTarget
     * @param selectionTargetSet
     */
    public PlayProcessingContext(IContext context, Map<String, Object> localVariables, Object selectionTarget, boolean selectionTargetSet) {
        super(context, localVariables, selectionTarget, selectionTargetSet);
    }

    /**
     * @param context
     * @param localVariables
     */
    public PlayProcessingContext(IContext context, Map<String, Object> localVariables) {
        super(context, localVariables);
    }

    /**
     * @param context
     */
    public PlayProcessingContext(IContext context) {
        super(context);
    }

    /**
     * @param processingContext
     */
    public PlayProcessingContext(IProcessingContext processingContext) {
        super(processingContext);
    }

    @Override
    public Map<String, Object> getBaseContextVariables() {
        OgnlContext context = new OgnlContext(super.getBaseContextVariables());
        context.setClassResolver(PlayClassResolver.INSTNACE);
        System.out.println("個展クスと＝" + context.getValues());
        return context;
    }
}
