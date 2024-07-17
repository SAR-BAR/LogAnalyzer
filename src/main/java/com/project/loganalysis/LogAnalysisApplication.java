//package com.project.loganalysis;
//
//import com.project.loganalysis.LogGeneration.LogFileGenerator;
//import com.project.loganalysis.LogGeneration.LogListener;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.context.annotation.ComponentScan;
//
//@SpringBootApplication
//@ComponentScan(basePackages = {"com.project.loganalysis"})
//public class LogAnalysisApplication {
//
//	public static void main(String[] args) {
//
//		SpringApplication.run(LogAnalysisApplication.class, args);
//		LogFileGenerator.startLogGeneration();
//
//		LogListener logListener = new LogListener();
//        logListener.startWatching();
//    }
//}

package com.project.loganalysis;

import com.project.loganalysis.LogImpl.LogFileGenerator;
import com.project.loganalysis.LogImpl.LogListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class LogAnalysisApplication {

	private final LogListener logListener;

	@Autowired
	public LogAnalysisApplication(LogListener logListener) {
		this.logListener = logListener;
	}

	public static void main(String[] args) {
		SpringApplication.run(LogAnalysisApplication.class, args);
		LogFileGenerator.startLogGeneration();
	}

	@PostConstruct
	public void startWatchingLogs() {
		logListener.startWatching();
	}
}
