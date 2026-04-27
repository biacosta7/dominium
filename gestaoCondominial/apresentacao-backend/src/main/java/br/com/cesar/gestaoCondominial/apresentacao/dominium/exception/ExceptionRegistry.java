package br.com.cesar.gestaoCondominial.apresentacao.dominium.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ExceptionRegistry {

    private final Map<Class<? extends Exception>, ExceptionEntry> registry = new HashMap<>();

    public void register(Class<? extends Exception> exceptionClass,
                         Class<? extends ExceptionHandlerStrategy> strategy,
                         HttpStatus status) {
        registry.put(exceptionClass, new ExceptionEntry(strategy, status));
    }

    public void register(Class<? extends Exception> exceptionClass, HttpStatus status) {
        this.register(exceptionClass, GenericExceptionHandlerStrategy.class, status);
    }

    private Optional<ExceptionHandlerStrategy> getExactMatchStrategy(Exception ex) {
        Class<?> cls = ex.getClass();
        ExceptionEntry entry = registry.get(cls);
        
        if (entry == null) return Optional.empty();

        return Optional.of(new GenericExceptionHandlerStrategy(ex, entry.getStatus()));
    }
    
    private Optional<ExceptionHandlerStrategy> getInheritanceMatchStrategy(Exception ex) {
        ExceptionEntry found = null;

        for (var e : registry.entrySet()) {
            Class<? extends Exception> registered = e.getKey();
            if (registered.isAssignableFrom(ex.getClass())) {
                found = e.getValue();
            }
        }

        if (found == null) return Optional.empty();

        return Optional.of(new GenericExceptionHandlerStrategy(ex, found.getStatus()));
    }

    public ExceptionHandlerStrategy getStrategy(Exception ex) {
        return getExactMatchStrategy(ex)
                .or(() -> getInheritanceMatchStrategy(ex))
                .orElse(new GenericExceptionHandlerStrategy(ex, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public void configureDefaults() {
        register(DomainException.class, HttpStatus.valueOf(422));
        register(ResourceNotFoundException.class, HttpStatus.NOT_FOUND);
        register(IllegalArgumentException.class, HttpStatus.BAD_REQUEST);
        register(IllegalStateException.class, HttpStatus.CONFLICT);
        register(RuntimeException.class, HttpStatus.INTERNAL_SERVER_ERROR);
        register(Exception.class, HttpStatus.INTERNAL_SERVER_ERROR);

        // Reserva Exceptions
        register(ReservaException.class, HttpStatus.valueOf(422));
        register(ConflitoReservaException.class, HttpStatus.CONFLICT);
        register(AreaNaoDisponivelException.class, HttpStatus.FORBIDDEN);
        register(CapacidadeExcedidaException.class, HttpStatus.CONFLICT);
    }
}
