package com.dominium.backend.domain.shared.exceptions;

import java.util.function.Supplier;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ExceptionHandler {
    
    private final ExceptionRegistry registry;

    public ExceptionHandler(ExceptionRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    public void init() {
        registry.configureDefaults();
    }

    @SuppressWarnings("unchecked")
    public <T> ResponseEntity<T> withHandler(Supplier<ResponseEntity<T>> callable) {
        try {
            return callable.get();
        } catch (Exception e) {
            ExceptionHandlerStrategy strategy = registry.getStrategy(e);
            return (ResponseEntity<T>) strategy.toResponseEntity();
        }
    }
}
