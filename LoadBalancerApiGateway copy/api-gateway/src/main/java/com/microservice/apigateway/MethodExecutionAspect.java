package com.microservice.apigateway;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MethodExecutionAspect {

    private static final Logger logger = LoggerFactory.getLogger(MethodExecutionAspect.class);

    @Before("execution(* com.microservice.apigateway..*(..))")
    public void logMethodCall(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String methodCall = "Chiamata al metodo: " + methodName + " con argomenti: " + args;
        logger.info(methodCall);
        //LogAnalyzerApp.addMethodCall(methodCall);
    }

    @AfterReturning(pointcut = "execution(* com.microservice.apigateway..*(..))", returning = "result")
    public void logMethodReturn(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String methodResult = "Ritorno dal metodo: " + methodName + " con risultato: " + result;
        logger.info(methodResult);
        //LogAnalyzerApp.addMethodResult(methodResult);
    }
}