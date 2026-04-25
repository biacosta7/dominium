package com.dominium.backend.domain.governanca.pauta;

import java.util.Objects;

public class PautaId {

    private final Long valor;

    public PautaId(Long valor) {
        this.valor = valor;
    }

    public static PautaId novo() {
        return new PautaId(null); // ou gerar ID se quiser
    }

    public static PautaId de(Long valor) {
        if (valor == null) {
            throw new IllegalArgumentException("ID da pauta não pode ser nulo");
        }
        return new PautaId(valor);
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
        if (!(o instanceof PautaId)) return false;
        PautaId pautaId = (PautaId) o;
        return Objects.equals(valor, pautaId.valor);
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