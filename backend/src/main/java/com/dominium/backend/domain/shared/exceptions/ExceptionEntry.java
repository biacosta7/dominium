package com.dominium.backend.domain.shared.exceptions;

import org.springframework.http.HttpStatus;

public class ExceptionEntry {
    private final Class<? extends ExceptionHandlerStrategy> strategyClass;
    private final HttpStatus status;

    public ExceptionEntry(Class<? extends ExceptionHandlerStrategy> strategyClass, HttpStatus status) {
        this.strategyClass = strategyClass;
        this.status = status;
    }

    public ExceptionEntry(HttpStatus status) {
        this.strategyClass = GenericExceptionHandlerStrategy.class;
        this.status = status;
    }

    public Class<? extends ExceptionHandlerStrategy> getStrategyClass() {
        return strategyClass;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
