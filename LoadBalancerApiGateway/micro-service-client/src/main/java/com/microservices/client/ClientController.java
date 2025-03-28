package com.microservices.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.extern.slf4j.Slf4j;


/**
 * Controller class for creating APIs which is present in the application.
 *
 * @author Vishnu Viswambharan
 * @version 1.0
 * @since 2020-06-01
 */

@Slf4j
@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ApiProxy apiProxy;

    @GetMapping("/technologyInfo/{platform}")
    public ResponseModel getTechnologyInfo(@PathVariable("platform") String platform) {

        // API calling using proxy interface and mapping into ResponseModel named Object.
        ResponseModel responseModel = apiProxy.retrieveTechnologyInfo(platform);

        return responseModel;
    }

    @GetMapping("/analyzeLanguage")
    public ResponseModel getLanguageAnalysis(
        @RequestParam(defaultValue = "Java") String language,
        @RequestParam(defaultValue = "ecosystem") String characteristic
    ) {
        log.info("Client requesting analysis for language: {} and characteristic: {}", language, characteristic);
        return apiProxy.analyzeLanguage(language, characteristic);
    }

    @GetMapping("/process")
    public ResponseModel processData(
        @RequestParam String operationType,
        @RequestParam String data,
        @RequestParam(required = false) Integer complexity
    ) {
        log.info("Client requesting operation: type={}, data={}, complexity={}", 
                operationType, data, complexity);
        return apiProxy.processOperation(operationType, data, complexity);
    }



}







