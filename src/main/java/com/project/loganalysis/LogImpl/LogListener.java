//package com.project.loganalysis.WatcherImpl;
//
//import com.project.loganalysis.QueueImpl.QueueProducer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//
//@Service
//public class LogListener {
//
//    private static final String[] LOG_FILE_PATHS = {
//            "logs/app1.log",
//            "logs/app2.log",
//            "logs/app3.log",
//    };
//
//
//
//    public void startWatching() {
//        for (String logFilePath : LOG_FILE_PATHS) {
//            // Get file path
//            Path logFile = Paths.get(logFilePath);
//            // Create new watcher with dependencies injected
//            Watcher watcherInstance = new Watcher(logFile);
//            // Activate the watcher
//            watcherInstance.start();
//            System.out.println("Started watching file: " + logFile);
//        }
//    }
//}

package com.project.loganalysis.LogImpl;

import com.project.loganalysis.QueueImpl.QueueProducer;
import com.project.loganalysis.WatcherImpl.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LogListener {

    private static final String[] LOG_FILE_PATHS = {
            "logs/app1.log",
            "logs/app2.log",
            "logs/app3.log",
    };

    private final QueueProducer queueProducer;

    @Autowired
    public LogListener(QueueProducer queueProducer) {
        this.queueProducer = queueProducer;
    }

    public void startWatching() {
        for (String logFilePath : LOG_FILE_PATHS) {
            // Get file path
            Path logFile = Paths.get(logFilePath);
            // Create new watcher with dependencies injected
            Watcher watcherInstance = new Watcher(logFile, queueProducer);
            // Activate the watcher
            watcherInstance.start();
            System.out.println("Started watching file: " + logFile);
        }
    }
}
