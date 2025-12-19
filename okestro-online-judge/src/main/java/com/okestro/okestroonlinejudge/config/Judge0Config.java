package com.okestro.okestroonlinejudge.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Judge0 API 설정.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "judge0")
public class Judge0Config {

    private String apiUrl;
    private String authnToken;
    private String authzToken;
    private Double cpuTimeLimit;
    private Integer memoryLimit;
    private Integer maxFileSize;
    private Integer maxRetries;
    private Integer retryDelayMs;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
