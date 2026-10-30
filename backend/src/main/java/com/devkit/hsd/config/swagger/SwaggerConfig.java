package com.devkit.hsd.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI().
                info(new Info().
                        title("HSD Community Management API").
                        description("JWT korumali API. Multi-tenant kurallar geregi tenant-bound kaynaklar topluluk scope'u icinde calisir; SUPER_ADMIN ise topluluklar arasi gozlem ve destek islemleri yapabilir.")).
                addSecurityItem(new SecurityRequirement().addList("bearerAuth")).
                components(new Components().addSecuritySchemes("bearerAuth",new SecurityScheme().
                        type(SecurityScheme.Type.HTTP).
                        scheme("bearer").bearerFormat("JWT").
                        description("JWT Token değerini giriniz.")));
    }

}
