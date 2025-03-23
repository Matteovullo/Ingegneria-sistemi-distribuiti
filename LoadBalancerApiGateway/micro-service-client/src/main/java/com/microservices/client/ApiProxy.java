package com.microservices.client;

import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Interface which act as a proxy, for communicating with API server.
 *
 * @author Vishnu Viswambharan
 * @version 1.0
 * @since 2020-06-01
 */

@FeignClient(name = "api-gateway-server")
@RibbonClient(name = "micro-service-server")
public interface ApiProxy {

    @GetMapping("micro-service-server/server/technologyInfo/{platform}")
    ResponseModel retrieveTechnologyInfo(@PathVariable("platform") String platform);

    @GetMapping("micro-service-server/server/languageAnalysis")
    ResponseModel analyzeLanguage(@RequestParam("language") String language, @RequestParam("characteristic") String characteristic
    );

        // In ApiProxy.java interface
    @GetMapping("micro-service-server/server/processOperation")
    ResponseModel processOperation(
        @RequestParam("operationType") String operationType,
        @RequestParam("data") String data,
        @RequestParam(required = false) Integer complexity
    );
}



