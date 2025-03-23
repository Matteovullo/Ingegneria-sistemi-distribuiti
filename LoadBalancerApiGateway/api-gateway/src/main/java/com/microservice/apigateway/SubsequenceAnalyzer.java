package com.microservice.apigateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SubsequenceAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(SubsequenceAnalyzer.class);

    // Registro delle chiamate ai metodi
    private final List<String> methodCallSequence = Collections.synchronizedList(new ArrayList<>());

    // Mappa per contare le frequenze delle sottosequenze
    private final Map<List<String>, Integer> subsequenceFrequency = new ConcurrentHashMap<>();

    // Dimensione massima della sottosequenza da analizzare
    private static final int MAX_SUBSEQUENCE_LENGTH = 3;

    /**
     * Registra una nuova chiamata al metodo.
     *
     * @param methodName Nome del metodo chiamato.
     */
    public synchronized void registerMethodCall(String methodName) {
        methodCallSequence.add(methodName);
        analyzeSubsequences(methodName);
    }

    /**
     * Analizza le sottosequenze di chiamate ai metodi.
     *
     * @param currentMethod Il metodo corrente che è stato chiamato.
     */
    private void analyzeSubsequences(String currentMethod) {
        // Estrai la sequenza recente di chiamate e crea una copia difensiva
        List<String> recentCalls = getRecentCalls(MAX_SUBSEQUENCE_LENGTH);

        // Genera tutte le possibili sottosequenze dalla sequenza recente
        for (int length = 2; length <= recentCalls.size(); length++) {
            for (int i = 0; i <= recentCalls.size() - length; i++) {
                // Crea una copia della sottosequenza per evitare problemi di modifica concorrente
                List<String> subsequence = new ArrayList<>(recentCalls.subList(i, i + length));

                // Incrementa il conteggio per questa sottosequenza
                subsequenceFrequency.put(subsequence, subsequenceFrequency.getOrDefault(subsequence, 0) + 1);
            }
        }

        // Visualizza le sottosequenze più comuni
        displayCommonSubsequences();
    }

    /**
     * Ottiene le ultime N chiamate dai metodi e restituisce una copia difensiva.
     *
     * @param count Numero di chiamate da recuperare.
     * @return Lista delle ultime N chiamate.
     */
    private List<String> getRecentCalls(int count) {
        int size = methodCallSequence.size();
        if (size < count) {
            return new ArrayList<>(methodCallSequence); // Copia difensiva
        }
        return new ArrayList<>(methodCallSequence.subList(size - count, size)); // Copia difensiva
    }

    /**
     * Visualizza le sottosequenze più comuni.
     */
    private void displayCommonSubsequences() {
        // Filtra solo le sottosequenze con frequenza maggiore di 1
        List<Map.Entry<List<String>, Integer>> commonSubsequences = subsequenceFrequency.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .sorted(Map.Entry.<List<String>, Integer>comparingByValue().reversed())
                .collect(Collectors.toList());

        // Logga le sottosequenze più comuni
        if (!commonSubsequences.isEmpty()) {
            logger.info("📈 SOTTOSEQUENZE PIÙ COMUNI:");
            for (Map.Entry<List<String>, Integer> entry : commonSubsequences) {
                logger.info("   Frequenza: {} | Sequenza: {}", entry.getValue(), entry.getKey());
            }
        }
    }
}