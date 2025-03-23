package com.microservice.apigateway;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ZuulLoggingFilter extends ZuulFilter {
    private static final Map<String, Long> operationTimings = new ConcurrentHashMap<>();
    //private static final Set<String> SENSITIVE_PARAMS = Set.of("password", "token", "secret", "authorization");

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String operationType = request.getParameter("operationType");

        if (operationType == null) {
            return false;
        }

        boolean isValid;
        switch (operationType.toLowerCase()) {
            case "analyze":
                isValid = validateAnalysisRequest(request);
                break;
            case "transform":
                isValid = validateTransformRequest(request);
                break;
            case "validate":
                isValid = validateValidationRequest(request);
                break;
            default:
                isValid = false;
        }

        return isValid;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String operationType = request.getParameter("operationType");

        if (operationType == null) {
            return null;
        }

        ZuulLoggingFilter proxy = applicationContext.getBean(ZuulLoggingFilter.class);

        long startTime = System.nanoTime();
        operationTimings.put(operationType, startTime);

        switch (operationType.toLowerCase()) {
            case "analyze":
                proxy.processAnalysisRequest(request);
                break;
            case "transform":
                proxy.processTransformRequest(request);
                break;
            case "validate":
                proxy.processValidationRequest(request);
                break;
        }

        return null;
    }

    public boolean validateAnalysisRequest(HttpServletRequest request) {
        String complexity = request.getParameter("complexity");
        try {
            return complexity == null || Integer.parseInt(complexity) <= 10;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean validateTransformRequest(HttpServletRequest request) {
        String data = request.getParameter("data");
        return data != null && data.length() <= 100;
    }

    public boolean validateValidationRequest(HttpServletRequest request) {
        String data = request.getParameter("data");
        return data != null && !data.isEmpty();
    }

    public void processAnalysisRequest(HttpServletRequest request) {
        String complexity = request.getParameter("complexity");
        simulateProcessingLoad(Integer.parseInt(complexity != null ? complexity : "1") * 100);
    }

    public void processTransformRequest(HttpServletRequest request) {
        String data = request.getParameter("data");
        simulateProcessingLoad(data.length() * 10);
    }

    public void processValidationRequest(HttpServletRequest request) {
        simulateProcessingLoad(50);
    }

    public void simulateProcessingLoad(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}