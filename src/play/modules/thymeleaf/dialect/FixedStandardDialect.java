package play.modules.thymeleaf.dialect;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.standard.StandardDialect;
import org.thymeleaf.standard.expression.StandardExpressionExecutor;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

/**
 * StandardDialect class except it uses PlayOgnlExpressionEvaluator instead of OgnlExpressionEvaluator.
 * 
 */

public class FixedStandardDialect extends StandardDialect {
    @Override
    public Map<String, Object> getExecutionAttributes() {
        final StandardExpressionExecutor executor = StandardExpressionProcessor.createStandardExpressionExecutor(PlayOgnlExpressionEvaluator.INSTANCE);
        final StandardExpressionParser parser = StandardExpressionProcessor.createStandardExpressionParser(executor);
        
        final Map<String,Object> executionAttributes = new HashMap<String, Object>();
        executionAttributes.put(
                StandardExpressionProcessor.STANDARD_EXPRESSION_EXECUTOR_ATTRIBUTE_NAME, executor);
        executionAttributes.put(
                StandardExpressionProcessor.STANDARD_EXPRESSION_PARSER_ATTRIBUTE_NAME, parser);
        
        return executionAttributes;
    }

}
