//package com.project.loganalysis.WatcherImpl;
//
//import java.io.IOException;
//import java.nio.file.*;
//import java.nio.file.Path;
//
//import com.project.loganalysis.QueueImpl.QueueProducer;
//import com.project.loganalysis.utils.GlobalLogger;
//import org.apache.logging.log4j.Level;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.PostConstruct;
//
//@Component
//@Service
//public class Watcher implements Runnable {
//
//
//    private final Path filePath;
//    private final Thread thread;
//    private boolean isRunning;
//    private static final Logger logger = GlobalLogger.getLoggerInstance();
//
//    public Watcher(Path filePath) {
//        this.filePath = filePath;
//        this.thread = new Thread(this);
//
//        //logger.info("Watcher initialized with QueueProducer: " + this.queueProducer);
//    }
//
//    @PostConstruct
//    public void init() {
//        logger.info("Watcher initialized with QueueProducer: " + this.queueProducer);
//    }
//
//    @Autowired
//    private QueueProducer queueProducer;
//
//
//    @Override
//    public void run() {
//        logger.info("QueueProducer instance: " + queueProducer);
//
//        if(queueProducer == null) {
//            System.err.println("QueueProducer is null");
//        }
//
//        try {
//            WatchService watchService = FileSystems.getDefault().newWatchService();
//            filePath.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
//            logger.log(Level.INFO, "Started watching *** " + filePath);
//
//            while (isRunning) {
//                WatchKey key = watchService.take();
//                for (WatchEvent<?> event : key.pollEvents()) {
//                    WatchEvent.Kind<?> kind = event.kind();
//                    if (kind == StandardWatchEventKinds.OVERFLOW) {
//                        System.err.println("Overflow event occurred");
//                        continue;
//                    }
//                    Path changed = (Path) event.context();
//                   // System.out.println("Event kind: " + kind + ", File changed: " + changed);
//                    if (filePath.getFileName().equals(changed)) {
//                        System.out.println("File change detected: " + filePath);
////                        logger.log(Level.INFO, "Log file changed: " + filePath);
//                        // Add your logic to process the change
//                        String logEntry = readLogEntry(filePath);
//                        queueProducer.sendMessage("logQueue", logEntry);
//                        System.out.println("Sent log entry to queue: " + logEntry);
//                    }
//                }
//                key.reset();
//            }
//            watchService.close();
//        } catch (InterruptedException | IOException e) {
//            logger.log(Level.INFO, "Watcher interrupted or error occurred: " + e.getMessage());
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
//    }
//
//    private String readLogEntry(Path filePath){
//        return "";
//    }
//}
//
//

package com.project.loganalysis.WatcherImpl;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.nio.file.Path;

import com.project.loganalysis.QueueImpl.QueueProducer;
import com.project.loganalysis.utils.GlobalLogger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class Watcher implements Runnable {

    private final Path filePath;
    private final Thread thread;
    private boolean isRunning;
    private static final Logger logger = GlobalLogger.getLoggerInstance();
    private final QueueProducer queueProducer;

    @Autowired
    public Watcher(Path filePath, QueueProducer queueProducer) {
        this.filePath = filePath;
        this.queueProducer = queueProducer;
        this.thread = new Thread(this);
    }

    @PostConstruct
    public void init() {
        logger.info("Watcher initialized with QueueProducer: " + this.queueProducer);
    }

    @Override
    public void run() {
        logger.info("QueueProducer instance: " + queueProducer);

        if (queueProducer == null) {
            System.err.println("QueueProducer is null");
        }

        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            filePath.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            logger.log(Level.INFO, "Started watching *** " + filePath);

            while (isRunning) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        System.err.println("Overflow event occurred");
                        continue;
                    }
                    Path changed = (Path) event.context();
                    if (filePath.getFileName().equals(changed)) {
                        System.out.println("File change detected: " + filePath);
                        String logEntry = readLogEntry(filePath);
                        queueProducer.sendMessage("logQueue", logEntry);
                        System.out.println("Sent log entry to queue: " + logEntry);
                    }
                }
                key.reset();
            }
            watchService.close();
        } catch (InterruptedException | IOException e) {
            logger.log(Level.INFO, "Watcher interrupted or error occurred: " + e.getMessage());
        }
    }

    public void start() {
        isRunning = true;
        thread.start();
    }

    public void stop() {
        isRunning = false;
        thread.interrupt();
    }

    private String readLogEntry(Path filePath) {
        String lastLine = "";
        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r")) {
            long fileLength = file.length() - 1;
            StringBuilder sb = new StringBuilder();

            for(long pointer = fileLength; pointer >= 0; pointer--){
                file.seek(pointer);
                char c;
                c = (char)file.read();
                if(c == '\n' && sb.length() > 0) {
                    break;
                }
                sb.append(c);
            }
            lastLine = sb.reverse().toString().trim();
        } catch (IOException e) {
            logger.log(Level.ERROR, "Error reading log entry from file: " + filePath, e);
        }
        return lastLine;
    }
}
