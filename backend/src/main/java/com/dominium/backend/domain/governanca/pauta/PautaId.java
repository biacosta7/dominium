package com.dominium.backend.domain.governanca.pauta;

import java.util.Objects;

public class PautaId {

    private final Long valor;

    public PautaId(Long valor) {
        if (valor == null) {
            throw new IllegalArgumentException("ID da pauta não pode ser nulo");
        }
        this.valor = valor;
    }

    public static PautaId novo() {
        return new PautaId(null); // ou gerar ID se quiser
    }

    public static PautaId de(Long valor) {
        return new PautaId(valor);
    }

    public Long getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PautaId)) return false;
        PautaId pautaId = (PautaId) o;
        return valor.equals(pautaId.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}