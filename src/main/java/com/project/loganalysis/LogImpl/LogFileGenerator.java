package com.project.loganalysis.LogImpl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogFileGenerator {
    private static final String[] LOG_FILE_PATHS = {
            "logs/app1.log",
            "logs/app2.log",
            "logs/app3.log"
    };

    //Number of threads
    private static final int NUM_THREADS = 3;

    public static void startLogGeneration(){
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);

        for (int i = 0; i < NUM_THREADS; i++) {
            int randomIndex = new Random().nextInt(LOG_FILE_PATHS.length);
            String logFilePath = LOG_FILE_PATHS[randomIndex];
            executorService.submit(new LogFileWriter(logFilePath));
        }

        executorService.shutdown();
    }
}

class LogFileWriter implements Runnable{
    private String logFilePath;

    public LogFileWriter(String logFilePath){
        this.logFilePath = logFilePath;
    }


    @Override
    public void run(){
        System.out.println("Generating log for file "+ logFilePath);

        try(PrintWriter printWriter = new PrintWriter(new FileWriter(logFilePath, true))){
            while(true){
                String logEntry = generateLogEntry();
               // System.out.println(logEntry);
                printWriter.println(logEntry);
                Thread.sleep(randomDelay());
                printWriter.flush();
            }

        }catch(IOException  | InterruptedException e){
            e.printStackTrace();
        }
    }

    private String generateLogEntry(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String timestamp = sdf.format(new Date());
        String threadName = "Thread-" + new Random().nextInt(3);
        String className = "com.project.loganalysis.LogGeneration";
        String logLevel = randomLogLevel();
        String message = "";

        switch (logLevel) {
            case "INFO":
                message = "User logged in successfully";
                message += String.format(" [userId=%d, ipAddress=192.168.%d.%d]",
                        new Random().nextInt(900) + 100,
                        new Random().nextInt(255) + 1,
                        new Random().nextInt(255) + 1);
                break;
            case "WARN":
                message = "Request timeout for API call";
                message += String.format(" [apiEndpoint=/api/data/%d, requestId=%d]",
                        new Random().nextInt(10),  // Example: Generate random endpoint ID
                        new Random().nextInt(900) + 100);
                break;
            case "ERROR":
                message = "Database connection failed";
                message += " [exception=" + generateDatabaseException() + "]";
                break;
        }

        String logEntry = String.format("%s [%s] %s %s - %s", timestamp, threadName, logLevel, className, message);
        return logEntry;
    }

    private String randomLogLevel(){
        String[] levels = {"INFO", "WARN", "ERROR"};
        return levels[new Random().nextInt(levels.length)];
    }

    private long randomDelay(){
        return new Random().nextInt(4000)+1000;
    }

    private String generateDatabaseException() {
        String[] exceptions = {
                "java.sql.SQLException: Connection refused",
                "java.sql.SQLException: Timeout expired",
                "java.sql.SQLException: Invalid username or password"

        };
        return exceptions[new Random().nextInt(exceptions.length)];
    }
}
