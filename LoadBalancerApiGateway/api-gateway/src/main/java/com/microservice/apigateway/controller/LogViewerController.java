package com.microservice.apigateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@RestController
public class LogViewerController {

    private static final String LOG_FILE_PATH = "/Users/matteovullo/Desktop/Uni/Ingegneria/LoadBalancerApiGateway/api-gateway/api-gateway/logs/application.log";

    @GetMapping("/logs")
    public ResponseEntity<String> getLogs() {
        StringBuilder logs = new StringBuilder();
        logs.append("<html><body><pre>"); // Inizio del contenuto HTML preformattato
        try (BufferedReader reader = new BufferedReader(new FileReader(LOG_FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logs.append(line).append("\n");
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Errore durante la lettura dei log: " + e.getMessage());
        }
        logs.append("</pre></body></html>"); // Fine del contenuto HTML preformattato
        return ResponseEntity.ok(logs.toString());
    }
}