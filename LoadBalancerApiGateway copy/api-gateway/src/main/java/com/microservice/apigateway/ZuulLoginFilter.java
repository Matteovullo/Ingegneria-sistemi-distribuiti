package com.microservice.apigateway;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * Class which act as a filter , in between API communications
 *
 * @author Vishnu Viswambharan
 * @version 1.0
 * @since 2020-06-01
 */
@Component
public class ZuulLoginFilter extends ZuulFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String filterType() {
        return "pre"; // filter before request is executed
        // return "post"; filter after request is executed
        //return "error"; upon request error
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    /* 
    @Override
    public Object run() throws ZuulException {
		logger.info("Request is filtered");
        HttpServletRequest httpServletRequest = RequestContext.getCurrentContext().getRequest();
        logger.info("request -> {} request uri -> {} ",
                httpServletRequest, httpServletRequest.getRequestURI());
        return null;
    }
    */

    @Override
    public Object run() throws ZuulException {
        logger.info("Request is filtered");
        HttpServletRequest httpServletRequest = RequestContext.getCurrentContext().getRequest();
        logger.info("request -> {} request uri -> {} ",
                httpServletRequest, httpServletRequest.getRequestURI());

        // Esegui operazioni aggiuntive
        performComplexOperation(httpServletRequest);
        validateRequest(httpServletRequest);
        checkAuthentication(httpServletRequest);
        logRequestHeaders(httpServletRequest);

        return null;
    }

    public void performComplexOperation(HttpServletRequest request) {
        logger.info("Chiamata al metodo: performComplexOperation");
        String userAgent = request.getHeader("User-Agent");
        String acceptLanguage = request.getHeader("Accept-Language");

        if (userAgent != null && acceptLanguage != null) {
            logger.info("User-Agent: {}", userAgent);
            logger.info("Accept-Language: {}", acceptLanguage);

            String combinedInfo = userAgent + " - " + acceptLanguage;
            logger.info("Combined Info: {}", combinedInfo);
        } else {
            logger.warn("User-Agent o Accept-Language non presenti nella richiesta");
        }
    }

    public void validateRequest(HttpServletRequest request) {
        logger.info("Chiamata al metodo: validateRequest");
        if (request.getHeader("X-Valid-Request") == null) {
            logger.warn("Request is missing X-Valid-Request header");
        } else {
            logger.info("Request is valid");
        }
    }

    public void checkAuthentication(HttpServletRequest request) {
        logger.info("Chiamata al metodo: checkAuthentication");
        if (request.getHeader("Authorization") == null) {
            logger.warn("Request is missing Authorization header");
        } else {
            logger.info("Request is authenticated");
        }
    }

    public void logRequestHeaders(HttpServletRequest request) {
        logger.info("Chiamata al metodo: logRequestHeaders");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            logger.info("{}: {}", headerName, request.getHeader(headerName));
        }
    }
}