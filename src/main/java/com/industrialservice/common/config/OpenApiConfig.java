package com.industrialservice.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI apiInfo() {
        return new OpenAPI().info(new Info().title("Industrial Machine Service API")
                .version("v1").description("Backend APIs for industrial machine service management."));
    }
}
