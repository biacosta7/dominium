package com.dominium.backend.domain.governanca.voto;

import com.dominium.backend.domain.governanca.pauta.PautaId;

import java.util.Objects;

public class VotoId {

    private final Long valor;

    public VotoId(Long valor) {
        if (valor == null) {
            throw new IllegalArgumentException("ID do voto não pode ser nulo");
        }
        this.valor = valor;
    }

    public static VotoId votar() {
        return new VotoId(null); // ou gerar ID se quiser
    }

    public static VotoId de(Long valor) {
        return new VotoId(valor);
    }

    public Long getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VotoId)) return false;
        VotoId votoId = (VotoId) o;
        return valor.equals(votoId.valor);
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
