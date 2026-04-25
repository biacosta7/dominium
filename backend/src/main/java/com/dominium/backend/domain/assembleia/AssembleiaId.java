package com.dominium.backend.domain.assembleia;

import java.util.Objects;
import java.util.UUID;

public class AssembleiaId {

    private final String valor;

    private AssembleiaId(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("ID da assembleia não pode ser vazio");
        }
        this.valor = valor;
    }

    public static AssembleiaId novo() {
        return new AssembleiaId(UUID.randomUUID().toString());
    }

    public static AssembleiaId de(String valor) {
        return new AssembleiaId(valor);
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AssembleiaId)) return false;
        AssembleiaId that = (AssembleiaId) o;
        return valor.equals(that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}
