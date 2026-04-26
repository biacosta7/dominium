package com.dominium.backend.domain.funcionario;

import java.util.Objects;
import java.util.UUID;

public class FuncionarioId {

    private final String valor;

    private FuncionarioId(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("ID do funcionário não pode ser vazio");
        }
        this.valor = valor;
    }

    public static FuncionarioId novo() {
        return new FuncionarioId(UUID.randomUUID().toString());
    }

    public static FuncionarioId de(String valor) {
        return new FuncionarioId(valor);
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FuncionarioId)) return false;
        FuncionarioId that = (FuncionarioId) o;
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
