package br.com.cesar.gestaoCondominial.dominio.dominium.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
