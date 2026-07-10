package com.lecturboxd.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EN: Defines OpenAPI / Swagger UI metadata and the JWT Bearer security scheme.
 * KA: განსაზღვრავს OpenAPI / Swagger UI მეტამონაცემებს და JWT Bearer უსაფრთხოების სქემას.
 */
@Configuration
public class OpenAPIConfig {

    /**
     * EN: Creates the OpenAPI bean with API info and global bearerAuth security requirement.
     * KA: ქმნის OpenAPI bean-ს API ინფორმაციით და გლობალური bearerAuth უსაფრთხოების მოთხოვნით.
     */
    @Bean
    public OpenAPI lecturBoxdOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("LecturBoxd API")
                        .description("API documentation for the LecturBoxd backend")
                        .version("v1")
                )
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }
}
