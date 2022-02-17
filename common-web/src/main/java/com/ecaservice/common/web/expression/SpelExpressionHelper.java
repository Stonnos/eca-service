package com.ecaservice.common.web.expression;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.stream.IntStream;

/**
 * Spring expression language expression helper.
 *
 * @author Roman Batygin
 */
public class SpelExpressionHelper {

    private final ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * Parses given expression.
     *
     * @param joinPoint  - join point
     * @param expression - expression
     * @return result value
     */
    public Object parseExpression(ProceedingJoinPoint joinPoint, String expression) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] methodParameters = methodSignature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        IntStream.range(0, methodParameters.length).forEach(i -> context.setVariable(methodParameters[i], args[i]));
        return expressionParser.parseExpression(expression).getValue(context, Object.class);
    }
}
