package com.project.loganalysis.utils;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlobalLogger {
    private static final Logger logger = LogManager.getLogger(GlobalLogger.class);

    public static Logger getLoggerInstance() {
        return logger;
    }
}

