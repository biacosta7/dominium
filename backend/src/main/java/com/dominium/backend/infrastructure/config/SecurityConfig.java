package com.dominium.backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/usuarios/**").permitAll()
                .requestMatchers("/unidades/**").permitAll()
                .requestMatchers("/notificacoes/**").permitAll()
                .requestMatchers("/funcionarios/**").permitAll()
                .requestMatchers("/ordens-servico/**").permitAll()
                .requestMatchers("/assembleias/**").permitAll()
                .requestMatchers("/multas/**").permitAll()
                .requestMatchers("/ocorrencias/**").permitAll()
                .requestMatchers("/reservas/**").permitAll()
                .requestMatchers("/pautas/**").permitAll()
                .requestMatchers("/votos/**").permitAll()
                .requestMatchers("/api/fila/**").permitAll()
                .requestMatchers("/api/unidades/**").permitAll()
                .requestMatchers("/api/moradores/**").permitAll()
                .requestMatchers("/financeiro/**").permitAll()
                .requestMatchers(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-ui.html"
                ).permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
