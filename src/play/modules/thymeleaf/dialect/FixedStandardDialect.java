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
