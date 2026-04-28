package br.com.cesar.gestaoCondominial.apresentacao.dominium.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Dominium API - Gestão Condominial")
                        .version("1.0.0")
                        .description("API para gerenciamento de condomínios, unidades, moradores e finanças.")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
