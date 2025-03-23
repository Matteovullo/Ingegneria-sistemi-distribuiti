package com.microservice.apigateway;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class AdvancedLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(AdvancedLoggingAspect.class);

    // Registro dei tempi di inizio dei metodi
    private static final ConcurrentHashMap<String, Long> methodStartTimes = new ConcurrentHashMap<>();

    // Istanza dell'analizzatore di sottosequenze
    private final SubsequenceAnalyzer subsequenceAnalyzer = new SubsequenceAnalyzer();

    // Punti di taglio
    @Pointcut("within(com.microservice.apigateway..*) || within(com.netflix.zuul.ZuulFilter+) || within(com.microservice.apigateway.ZuulLoggingFilter)")
    public void allMethods() {}

    @Pointcut("execution(* com.microservice.apigateway.ZuulLoggingFilter.*(..))")
    public void zuulLoggingFilterMethods() {}

    @Pointcut("within(com.microservice.apigateway.service..*) || within(com.microservice.apigateway.repository..*)")
    public void serviceAndRepositoryMethods() {}

    @Pointcut("execution(* com.netflix.zuul.ZuulFilter+.*(..))")
    public void zuulFilterMethods() {}

    /**
     * Logga l'inizio dell'esecuzione di un metodo.
     */
    @Before("allMethods() || zuulLoggingFilterMethods()")
    public void logMethodEntry(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();

        // Estrai i parametri del metodo
        Object[] args = maskSensitiveParameters(joinPoint.getArgs());
        String params = Arrays.toString(args);

        // Registra il metodo nell'analizzatore di sottosequenze
        subsequenceAnalyzer.registerMethodCall(methodName);

        // Logga l'inizio del metodo
        logger.info("🚀 INIZIO: {} | Parametri: {}", methodName, params);

        // Memorizza il tempo di inizio
        methodStartTimes.put(methodName, System.nanoTime());
    }

    /**
     * Logga la fine dell'esecuzione di un metodo con successo.
     */
    @AfterReturning(pointcut = "allMethods() || zuulLoggingFilterMethods()", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();

        // Calcola il tempo di esecuzione
        long endTime = System.nanoTime();
        long duration = (endTime - methodStartTimes.getOrDefault(methodName, endTime)) / 1_000_000;

        // Logga il completamento del metodo
        logger.info("✅ SUCCESSO: {} | Ritorno: {} | Tempo esecuzione: {} ms", methodName, result, duration);
    }

    /**
     * Logga le eccezioni generate durante l'esecuzione di un metodo.
     */
    @AfterThrowing(pointcut = "allMethods() || zuulLoggingFilterMethods()", throwing = "ex")
    public void logMethodException(JoinPoint joinPoint, Throwable ex) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();

        // Estrai i parametri del metodo
        Object[] args = maskSensitiveParameters(joinPoint.getArgs());
        String params = Arrays.toString(args);

        // Logga l'eccezione
        logger.error("❌ ERRORE: {} | Parametri: {} | Eccezione: {}", methodName, params, ex.getMessage());
    }

    /**
     * Logga la completazione di un metodo, indipendentemente dal risultato.
     */
    @After("allMethods() || zuulLoggingFilterMethods()")
    public void logMethodCompletion(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();

        // Logga il completamento del metodo
        logger.info("🔄 COMPLETATO: {}", methodName);
    }

    /**
     * Misura il tempo di esecuzione di un metodo.
     */
    @Around("allMethods() || zuulLoggingFilterMethods()")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        Object result;

        try {
            result = joinPoint.proceed();
        } finally {
            long end = System.nanoTime();
            long durationMillis = (end - start) / 1_000_000;

            // Estrai il nome del metodo
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.getDeclaringTypeName() + "." + signature.getName();

            // Logga il tempo di esecuzione
            logger.info("⏳ TEMPO DI ESECUZIONE: {} -> {} ms", methodName, durationMillis);
        }

        return result;
    }

    /**
     * Maschera i parametri sensibili prima di registrarli nei log.
     *
     * @param args Array di parametri del metodo.
     * @return Array di parametri mascherati.
     */
    private Object[] maskSensitiveParameters(Object[] args) {
        if (args == null) return new Object[0];

        return Arrays.stream(args)
                .map(arg -> {
                    if (arg instanceof String && ((String) arg).contains("password")) {
                        return "****"; // Maschera i valori sensibili
                    }
                    return arg;
                })
                .toArray();
    }
}