/* 
package com.microservice.apigateway;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LogAnalyzer {

    public static List<String> readLog(String filePath) {
        List<String> logEntries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Chiamata al metodo:") || line.contains("Ritorno dal metodo:")) {
                    logEntries.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logEntries;
    }

    /* 
    public static Map<String, Integer> analyzeAllSubsequences(List<String> logEntries, int maxLength) {
        Map<String, Integer> subsequences = new HashMap<>();
        List<String> methodCalls = new ArrayList<>();
        for (String entry : logEntries) {
            if (entry.contains("Chiamata al metodo:")) {
                String methodName = entry.split("Chiamata al metodo: ")[1].split(" con argomenti:")[0];
                methodCalls.add(methodName);
            }
        }
        for (int length = 1; length <= Math.min(maxLength, methodCalls.size()); length++) {
            for (int i = 0; i <= methodCalls.size() - length; i++) {
                StringBuilder subsequence = new StringBuilder();
                for (int j = 0; j < length; j++) {
                    if (j > 0) {
                        subsequence.append(" -> ");
                    }
                    subsequence.append(methodCalls.get(i + j));
                }
                String subsequenceStr = subsequence.toString();
                subsequences.put(subsequenceStr, subsequences.getOrDefault(subsequenceStr, 0) + 1);
            }
        }
        return subsequences;
    }


    public static void printMostCommonSubsequences(Map<String, Integer> subsequences, int topN) {
        subsequences.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(topN)
            .forEach(entry -> System.out.println("Sottosequenza: " + entry.getKey() + ", Conteggio: " + entry.getValue()));
    }
    
    public static void printAllSubsequences(Map<String, Integer> subsequences) {
        subsequences.forEach((key, value) -> System.out.println("Sottosequenza: " + key + ", Conteggio: " + value));
    }

    public static Map<String, Integer> analyzeAllSubsequences(List<String> logEntries, int maxLength) {
        Map<String, Integer> subsequences = new HashMap<>();
        List<String> methodCalls = new ArrayList<>();
        for (String entry : logEntries) {
            if (entry.contains("Chiamata al metodo:")) {
                // Estrai il nome del metodo dal log
                String methodName = entry.split("Chiamata al metodo: ")[1].split(" ")[0];
                methodCalls.add(methodName);
            }
        }
        for (int length = 1; length <= Math.min(maxLength, methodCalls.size()); length++) {
            for (int i = 0; i <= methodCalls.size() - length; i++) {
                StringBuilder subsequence = new StringBuilder();
                for (int j = 0; j < length; j++) {
                    if (j > 0) {
                        subsequence.append(" -> ");
                    }
                    subsequence.append(methodCalls.get(i + j));
                }
                String subsequenceStr = subsequence.toString();
                subsequences.put(subsequenceStr, subsequences.getOrDefault(subsequenceStr, 0) + 1);
            }
        }
        return subsequences;
    }
    
    public static void main(String[] args) {
        String filePath = "/Users/matteovullo/Desktop/Uni/Ingegneria/LOAD-BALANCER-WITH-API-GATEWAY copy/api-gateway/api-gateway/logs/application.log";
        int maxLength = 5; // Lunghezza massima delle sottosequenze
        List<String> logEntries = readLog(filePath);
        Map<String, Integer> subsequences = analyzeAllSubsequences(logEntries, maxLength);
        printAllSubsequences(subsequences);
    }
}
*/

package com.microservice.apigateway;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class LogAnalyzer {

    public static List<String> readLog(String filePath) {
        List<String> logEntries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("Chiamata al metodo:") || line.contains("Ritorno dal metodo:")) {
                    logEntries.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logEntries;
    }

    public static Map<String, Integer> analyzeAllSubsequences(List<String> logEntries, int maxLength) {
        Map<String, Integer> subsequences = new HashMap<>();
        List<String> methodCalls = new ArrayList<>();
        for (String entry : logEntries) {
            if (entry.contains("Chiamata al metodo:")) {
                // Estrai il nome del metodo dal log
                String methodName = entry.split("Chiamata al metodo: ")[1].split(" ")[0];
                methodCalls.add(methodName);
            }
        }
        for (int length = 1; length <= Math.min(maxLength, methodCalls.size()); length++) {
            for (int i = 0; i <= methodCalls.size() - length; i++) {
                StringBuilder subsequence = new StringBuilder();
                for (int j = 0; j < length; j++) {
                    if (j > 0) {
                        subsequence.append(" -> ");
                    }
                    subsequence.append(methodCalls.get(i + j));
                }
                String subsequenceStr = subsequence.toString();
                subsequences.put(subsequenceStr, subsequences.getOrDefault(subsequenceStr, 0) + 1);
            }
        }
        return subsequences;
    }

    public static void printMostCommonSubsequences(Map<String, Integer> subsequences, int topN) {
        subsequences.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(topN)
            .forEach(entry -> System.out.println("Sottosequenza: " + entry.getKey() + ", Conteggio: " + entry.getValue()));
    }
    
    public static void printAllSubsequences(Map<String, Integer> subsequences) {
        subsequences.forEach((key, value) -> System.out.println("Sottosequenza: " + key + ", Conteggio: " + value));
    }

    public static void main(String[] args) {
        String filePath = "/Users/matteovullo/Desktop/Uni/Ingegneria/LOAD-BALANCER-WITH-API-GATEWAY copy/api-gateway/api-gateway/logs/application.log";
        int maxLength = 5; // Lunghezza massima delle sottosequenze
        List<String> logEntries = readLog(filePath);
        Map<String, Integer> subsequences = analyzeAllSubsequences(logEntries, maxLength);
        printAllSubsequences(subsequences);
    }
}