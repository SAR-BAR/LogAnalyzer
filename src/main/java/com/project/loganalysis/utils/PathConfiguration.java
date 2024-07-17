package com.project.loganalysis.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class PathConfiguration {

    @Bean
    public Path filePath() {
        // Example: Define a path to a specific directory
        return Paths.get("/path/to/your/directory");
    }
}

