package com.example.bankcards.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Card Management API")
                        .version("1.0.0")
                        .description("API для управления банковскими картами: аутентификация, CRUD карт, переводы между своими картами"))
                .addServersItem(new Server().url("http://localhost:8080").description("Local Development Server"));
    }

}
