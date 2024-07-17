package com.project.loganalysis.WatcherImpl;

import java.nio.file.Path;

public class FileChangeEvent {
    private Path filePath;
    private String logEntry;

    public FileChangeEvent(Path filePath, String logEntry) {
        this.filePath = filePath;
        this.logEntry = logEntry;
    }

    public Path getFilePath() {
        return filePath;
    }

    public void setFilePath(Path filePath) {
        this.filePath = filePath;
    }

    public String getLogEntry() {
        return logEntry;
    }

    public void setLogEntry(String logEntry) {
        this.logEntry = logEntry;
    }

    @Override
    public String toString() {
        return "FileChangeEvent{" +
                "filePath=" + filePath +
                ", logEntry='" + logEntry + '\'' +
                '}';
    }
}

