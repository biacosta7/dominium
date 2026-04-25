package com.dominium.backend.domain.governanca.voto;

import com.dominium.backend.domain.governanca.pauta.PautaId;

import java.util.Objects;

public class VotoId {

    private final Long valor;

    private VotoId(Long valor) {
        this.valor = valor;
    }

    public static VotoId novo() {
        return new VotoId(null);
    }
    
    public static VotoId de(Long valor) {
        if (valor == null) {
            throw new IllegalArgumentException("ID do voto não pode ser nulo");
        }
        return new VotoId(valor);
    }

    public Long getValor() {
        return valor;
    }

    public boolean temValor() {
        return valor != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VotoId)) return false;
        VotoId votoId = (VotoId) o;
        return Objects.equals(valor, votoId.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor != null ? valor.toString() : "sem-id";
    }
}
