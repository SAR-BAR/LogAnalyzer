//package com.project.loganalysis.WatcherImpl;
//
//import java.io.IOException;
//import java.nio.file.*;
//import java.util.HashMap;
//import java.util.Map;
//
//import com.project.loganalysis.QueueImpl.QueueProducer;
//import com.project.loganalysis.utils.GlobalLogger;
//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.Logger;
//
//public class DirectoryWatcher implements Runnable {
//
//    private final Path dir;
//    private final WatchService watchService;
//    private final Thread thread;
//    private boolean isRunning;
//    private static final Logger logger = GlobalLogger.getLoggerInstance();
//
//
//    // Map to store watchers for individual files
//    private final Map<Path, Watcher> watchersMap = new HashMap<>();
//
//    // Instantiate DirectoryWatcher instance
//    public DirectoryWatcher(Path dir) throws IOException {
//        this.dir = dir;
//        this.watchService = FileSystems.getDefault().newWatchService();
//        this.thread = new Thread(this);
//    }
//
//    @Override
//    public void run() {
//        try {
//            // Register watchService to watch modify events on the directory
//            dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
//            logger.log(Level.INFO, "Started watching directory: " + dir);
//
//            while (isRunning) {
//                // Waits for events to happen
//                WatchKey key = watchService.take();
//                // Retrieves and removes all pending events
//                for (WatchEvent<?> event : key.pollEvents()) {
//                    WatchEvent.Kind<?> kind = event.kind();
//                    if (kind == StandardWatchEventKinds.OVERFLOW) {
//                        continue;
//                    }
//                    // Get path of file that triggered the event
//                    Path changedFile = (Path) event.context();
//                    Path resolvedFilePath = dir.resolve(changedFile);
//                    // Handle only files, not directories
//                    if (Files.isRegularFile(resolvedFilePath)) {
//                        handleFileChange(resolvedFilePath);
//                    }
//                }
//                // Reset the key
//                boolean valid = key.reset();
//                if (!valid) {
//                    break;
//                }
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        } catch (IOException e) {
//            logger.log(Level.ERROR, "Error watching directory: " + dir, e);
//        } finally {
//            try {
//                watchService.close();
//            } catch (IOException e) {
//                logger.log(Level.ERROR, "Error closing watch service for directory: " + dir, e);
//            }
//        }
//    }
//
//    private void handleFileChange(Path filePath) {
//        synchronized (watchersMap) {
//            if (!watchersMap.containsKey(filePath)) {
//                Watcher watcher = new Watcher(filePath);
//                watchersMap.put(filePath, watcher);
//                watcher.start();
//                System.out.println("Started watching file: " + filePath);
//            }
//        }
//    }
//
//    public void start() {
//        isRunning = true;
//        thread.start();
//    }
//
//    public void stop() {
//        isRunning = false;
//        thread.interrupt();
//        try {
//            watchService.close();
//        } catch (IOException e) {
//            logger.log(Level.ERROR, "Error closing watch service for directory: " + dir, e);
//        }
//    }
//}
//
//


package com.project.loganalysis.WatcherImpl;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

import com.project.loganalysis.QueueImpl.QueueProducer;
import com.project.loganalysis.utils.GlobalLogger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class DirectoryWatcher implements Runnable {

    private final Path dir;
    private final WatchService watchService;
    private final Thread thread;
    private boolean isRunning;
    private static final Logger logger = GlobalLogger.getLoggerInstance();
    private final QueueProducer queueProducer;

    // Map to store watchers for individual files
    private final Map<Path, Watcher> watchersMap = new HashMap<>();

    // Instantiate DirectoryWatcher instance
    public DirectoryWatcher(Path dir, QueueProducer queueProducer) throws IOException {
        this.dir = dir;
        this.queueProducer = queueProducer;
        this.watchService = FileSystems.getDefault().newWatchService();
        this.thread = new Thread(this);
    }

    @Override
    public void run() {
        try {
            // Register watchService to watch modify events on the directory
            dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            logger.log(Level.INFO, "Started watching directory: " + dir);

            while (isRunning) {
                // Waits for events to happen
                WatchKey key = watchService.take();
                // Retrieves and removes all pending events
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    // Get path of file that triggered the event
                    Path changedFile = (Path) event.context();
                    Path resolvedFilePath = dir.resolve(changedFile);
                    // Handle only files, not directories
                    if (Files.isRegularFile(resolvedFilePath)) {
                        handleFileChange(resolvedFilePath);
                    }
                }
                // Reset the key
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error watching directory: " + dir, e);
        } finally {
            try {
                watchService.close();
            } catch (IOException e) {
                logger.log(Level.ERROR, "Error closing watch service for directory: " + dir, e);
            }
        }
    }

    private void handleFileChange(Path filePath) {
        synchronized (watchersMap) {
            if (!watchersMap.containsKey(filePath)) {
                Watcher watcher = new Watcher(filePath, queueProducer);
                watchersMap.put(filePath, watcher);
                watcher.start();
                System.out.println("Started watching file: " + filePath);
            }
        }
    }

    public void start() {
        isRunning = true;
        thread.start();
    }

    public void stop() {
        isRunning = false;
        thread.interrupt();
        try {
            watchService.close();
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error closing watch service for directory: " + dir, e);
        }
    }
}
