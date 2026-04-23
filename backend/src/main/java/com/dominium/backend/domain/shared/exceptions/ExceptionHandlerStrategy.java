package com.dominium.backend.domain.shared.exceptions;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface ExceptionHandlerStrategy {
    String getMessage();
    String getName();
    ResponseEntity<Map<String, String>> toResponseEntity();
    HttpStatus getStatusCode();
    Exception getOriginalException();
}
