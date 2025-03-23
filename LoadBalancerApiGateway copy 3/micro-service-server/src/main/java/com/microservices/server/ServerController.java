package com.microservices.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
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

 /*
ServerController (Server):
Esporta tre endpoint principali:
    /languageAnalysis: Analizza il linguaggio di programmazione e le sue caratteristiche (performance, ecosistema).
    /processOperation: Esegue operazioni su dati (analisi, trasformazione, validazione).
    /technologyInfo/{platform}: Fornisce informazioni su una tecnologia specifica (come Java o Python).
    Usa il pacchetto Environment per recuperare la porta del server in uso.
ClientController (Client):
Comunica con il server tramite il proxy ApiProxy:
    /technologyInfo/{platform}: Ottiene informazioni sulla tecnologia specificata.
    /analyzeLanguage: Richiede l'analisi di un linguaggio di programmazione.
    /process: Richiede l'esecuzione di un'operazione sui dati.
  */

@Slf4j
@RestController
@RequestMapping("/server")
public class ServerController {

    @Autowired
    private Environment environment;

    @GetMapping("/languageAnalysis")
    public ResponseModel analyzeLanguage(@RequestParam String language, @RequestParam String characteristic) {
        log.info("Analyzing language: {} for characteristic: {}", language, characteristic);
        ResponseModel responseModel = new ResponseModel();
        responseModel.setTittle("Language Analysis");
        responseModel.setPlatform(language);
        
        switch (characteristic.toLowerCase()) {
            case "performance":
                if (language.equalsIgnoreCase("C++")) {
                    log.info("Analyzing C++ performance characteristics");
                    responseModel.setUsedFor("High-performance computing and system programming");
                    responseModel.setAnalysisResult("Excellent performance with direct memory management");
                } else if (language.equalsIgnoreCase("Python")) {
                    log.info("Analyzing Python performance characteristics");
                    responseModel.setUsedFor("Rapid development and prototyping");
                    responseModel.setAnalysisResult("Good for most applications, may need optimization for heavy computations");
                } else {
                    log.info("Analyzing general performance for: {}", language);
                    responseModel.setUsedFor("General purpose programming");
                    responseModel.setAnalysisResult("Performance characteristics unknown");
                }
                break;
                
            case "ecosystem":
                if (language.equalsIgnoreCase("JavaScript")) {
                    log.info("Analyzing JavaScript ecosystem");
                    responseModel.setUsedFor("Web development and full-stack applications");
                    responseModel.setAnalysisResult("Vast ecosystem with npm and numerous frameworks");
                } else if (language.equalsIgnoreCase("Java")) {
                    log.info("Analyzing Java ecosystem");
                    responseModel.setUsedFor("Enterprise applications and Android development");
                    responseModel.setAnalysisResult("Rich ecosystem with Maven/Gradle and enterprise frameworks");
                } else {
                    log.info("Analyzing general ecosystem for: {}", language);
                    responseModel.setUsedFor("Specific domain programming");
                    responseModel.setAnalysisResult("Ecosystem details not available");
                }
                break;
                
            default:
                log.warn("Unknown characteristic requested: {}", characteristic);
                responseModel.setUsedFor("Unknown analysis type");
                responseModel.setAnalysisResult("Characteristic not recognized");
        }
        
        responseModel.setServerPort(Short.parseShort(environment.getProperty("local.server.port")));
        return responseModel;
    }

    @GetMapping("/processOperation")
    public ResponseModel processOperation(
        @RequestParam String operationType,
        @RequestParam String data,
        @RequestParam(required = false) Integer complexity
    ) {
        log.info("Starting operation processing: type={}, data={}, complexity={}", 
                operationType, data, complexity);
        
        ResponseModel response = new ResponseModel();
        response.setTittle("Operation Result");
        
        switch (operationType.toLowerCase()) {
            case "analyze":
                return handleAnalysis(data, complexity);
            case "transform":
                return handleTransformation(data, complexity);
            case "validate":
                return handleValidation(data, complexity);
            default:
                log.warn("Unknown operation type requested: {}", operationType);
                response.setUsedFor("Unknown operation");
                response.setAnalysisResult("Operation type not recognized");
                return response;
        }
    }

    private ResponseModel handleAnalysis(String data, Integer complexity) {
        log.info("Starting data analysis process");
        ResponseModel response = new ResponseModel();
        
        if (complexity != null && complexity > 5) {
            log.info("Performing complex analysis with depth {}", complexity);
            performComplexAnalysis(data);
            response.setAnalysisResult("Complex analysis completed");
        } else {
            log.info("Performing simple analysis");
            performSimpleAnalysis(data);
            response.setAnalysisResult("Simple analysis completed");
        }
        
        response.setTittle("Analysis Result");
        response.setUsedFor("Data Analysis");
        return response;
    }

    private ResponseModel handleTransformation(String data, Integer complexity) {
        log.info("Starting data transformation process");
        ResponseModel response = new ResponseModel();
        
        if (data.length() > 10) {
            log.info("Processing long data string: length={}", data.length());
            response.setAnalysisResult(transformLongData(data));
        } else {
            log.info("Processing short data string");
            response.setAnalysisResult(transformShortData(data));
        }
        
        response.setTittle("Transformation Result");
        response.setUsedFor("Data Transformation");
        return response;
    }

    private ResponseModel handleValidation(String data, Integer complexity) {
        log.info("Starting validation process");
        ResponseModel response = new ResponseModel();
        
        if (data.matches("^[a-zA-Z0-9]*$")) {
            log.info("Validating alphanumeric data");
            response.setAnalysisResult("Valid alphanumeric data");
        } else {
            log.warn("Invalid data format detected");
            response.setAnalysisResult("Invalid data format");
        }
        
        response.setTittle("Validation Result");
        response.setUsedFor("Data Validation");
        return response;
    }

    private void performComplexAnalysis(String data) {
        log.info("Executing complex analysis steps");
        // Simulate multiple processing steps
        for (int i = 1; i <= 3; i++) {
            log.info("Complex analysis step {}: Processing data chunk", i);
            try {
                Thread.sleep(100); // Simulate processing time
            } catch (InterruptedException e) {
                log.error("Analysis interrupted", e);
            }
        }
    }

    private void performSimpleAnalysis(String data) {
        log.info("Executing simple analysis");
        try {
            Thread.sleep(50); // Simulate processing time
        } catch (InterruptedException e) {
            log.error("Analysis interrupted", e);
        }
    }

    private String transformLongData(String data) {
        log.info("Applying long data transformation");
        return "Transformed: " + data.substring(0, 10) + "...";
    }

    private String transformShortData(String data) {
        log.info("Applying short data transformation");
        return "Transformed: " + data;
    }

    @GetMapping("/technologyInfo/{platform}")
    public ResponseModel retrieveTechnologyInfo(@PathVariable("platform") String platform) {
        ResponseModel responseModel = new ResponseModel();

        if (platform.equalsIgnoreCase("Java")) {
            responseModel.setTittle("Technology Stack");
            responseModel.setPlatform("Java");
            responseModel.setUsedFor("Secured Web Services");
        } else if (platform.equalsIgnoreCase("python")) {
            responseModel.setTittle("Technology Stack");
            responseModel.setPlatform("python");
            responseModel.setUsedFor("Machine Learning");
        } else {
            responseModel.setTittle("Technology Stack");
            responseModel.setPlatform(platform);
            responseModel.setUsedFor("Unknown platform");
        }

        responseModel.setServerPort(Short.parseShort(environment.getProperty("local.server.port")));

        return responseModel;

    }

    ;
}



