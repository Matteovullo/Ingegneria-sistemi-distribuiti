package com.microservice.apigateway;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LogAnalyzerUI extends Application {

    private static final String FILE_PATH = "/Users/matteovullo/Desktop/Uni/Ingegneria/LoadBalancerApiGateway/api-gateway/api-gateway/logs/application.log";

    private TextArea logArea;
    private ListView<String> subsequencesList;
    private Thread logWatcherThread;
    private volatile boolean running = true;
    private long filePosition = 0;
    private List<String> allLogEntries = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Real-Time Log Analyzer");

        VBox layout = new VBox(15);
        layout.setPadding(new javafx.geometry.Insets(20));

        // Inizializza i componenti UI
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(600);
        logArea.setPrefWidth(1000);
        logArea.setWrapText(true);

        Button clearButton = new Button("Clear Logs");
        clearButton.setOnAction(event -> clearLogs());

        Label sequencesLabel = new Label("Method Sequences:");
        subsequencesList = new ListView<>();
        subsequencesList.setPrefHeight(400);

        layout.getChildren().addAll(clearButton, logArea, sequencesLabel, subsequencesList);

        Scene scene = new Scene(layout, 1200, 900);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Inizia il monitoraggio del file di log
        startFileMonitoring();
    }

    private void clearLogs() {
        Platform.runLater(() -> {
            logArea.clear();
            subsequencesList.getItems().clear();
            allLogEntries.clear();
            filePosition = 0;
        });
    }

    private void startFileMonitoring() {
        logWatcherThread = new Thread(() -> {
            try {
                Path logFile = Paths.get(FILE_PATH);
                Path parentDir = logFile.getParent();

                WatchService watchService = FileSystems.getDefault().newWatchService();
                parentDir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

                // Leggi il contenuto iniziale del file
                readInitialContent();

                while (running) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            Path changed = (Path) event.context();
                            if (logFile.getFileName().equals(changed)) {
                                // File modificato, leggi il nuovo contenuto
                                readNewContent();
                            }
                        }
                    }
                    key.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        logWatcherThread.setDaemon(true);
        logWatcherThread.start();
    }

    private void readInitialContent() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processLogLine(line);
            }
            filePosition = new File(FILE_PATH).length();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readNewContent() {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(FILE_PATH, "r")) {
            randomAccessFile.seek(filePosition);
            String line;

            // Leggi le nuove linee aggiunte al file
            while ((line = randomAccessFile.readLine()) != null) {
                final String currentLine = line;
                processLogLine(currentLine);
            }

            filePosition = randomAccessFile.getFilePointer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processLogLine(String line) {
        if (isRelevantLogLine(line)) {
            Platform.runLater(() -> {
                logArea.appendText(line + "\n");
                allLogEntries.add(line);
                updateSequenceAnalysis();
            });
        }
    }

    private boolean isRelevantLogLine(String line) {
        return line.contains("🚀 INIZIO:") || 
               line.contains("✅ SUCCESSO:") || 
               line.contains("❌ ERRORE:") || 
               line.contains("🔄 COMPLETATO:") ||
               line.contains("filterType") ||
               line.contains("shouldFilter") ||
               line.contains("run");
    }

    private void updateSequenceAnalysis() {
        Map<String, Integer> sequences = analyzeMethodSequences(allLogEntries);

        Platform.runLater(() -> {
            subsequencesList.getItems().clear();
            sequences.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> {
                    String formatted = String.format("%s (Count: %d)", entry.getKey(), entry.getValue());
                    subsequencesList.getItems().add(formatted);
                });
        });
    }

    private Map<String, Integer> analyzeMethodSequences(List<String> logEntries) {
        Map<String, Integer> sequences = new HashMap<>();
        List<String> methodCalls = new ArrayList<>();

        // Estrai le chiamate ai metodi dai log
        for (String entry : logEntries) {
            if (entry.contains("🚀 INIZIO:")) {
                String methodName = extractMethodName(entry);
                if (methodName != null) {
                    methodCalls.add(methodName);
                }
            }
        }

        // Analizza sequenze di lunghezza 1-3
        for (int length = 1; length <= Math.min(3, methodCalls.size()); length++) {
            for (int i = 0; i <= methodCalls.size() - length; i++) {
                StringBuilder sequence = new StringBuilder();
                for (int j = 0; j < length; j++) {
                    if (j > 0) sequence.append(" → ");
                    sequence.append(methodCalls.get(i + j));
                }
                sequences.merge(sequence.toString(), 1, Integer::sum);
            }
        }

        return sequences;
    }

    private String extractMethodName(String logLine) {
        try {
            String parts = logLine.split("🚀 INIZIO: ")[1].split("\\|")[0].trim();
            return parts.split("\\.")[parts.split("\\.").length - 1];
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void stop() {
        running = false;
        if (logWatcherThread != null) {
            logWatcherThread.interrupt();
        }
    }
}