package br.com.cesar.gestaoCondominial.dominio.dominium.valueobjects;

import java.util.Objects;
import com.dominium.backend.domain.shared.exceptions.DomainException;
import lombok.Getter;

/**
 * Classe base abstrata para Value Objects de ID.
 * Fornece implementação comum de equals, hashCode e toString.
 */
@Getter
public abstract class ValueObjectId<T> {
    
    private final T valor;
    
    protected ValueObjectId(T valor) {
        if (valor instanceof Integer integer && integer < 0) {
            throw new DomainException("ID não pode ser negativo");
        }
        if (valor instanceof Long longVal && longVal < 0) {
            throw new DomainException("ID não pode ser negativo");
        }
        this.valor = valor;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueObjectId<?> that = (ValueObjectId<?>) o;
        return Objects.equals(valor, that.valor);
    }
    
    public T getId() {
        return valor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + valor + ")";
    }
}
