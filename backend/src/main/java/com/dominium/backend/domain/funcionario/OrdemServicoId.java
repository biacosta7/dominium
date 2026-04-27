package com.dominium.backend.domain.funcionario;

import java.util.Objects;
import java.util.UUID;

public class OrdemServicoId {

    private final String valor;

    private OrdemServicoId(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("ID da ordem de serviço não pode ser vazio");
        }
        this.valor = valor;
    }

    public static OrdemServicoId novo() {
        return new OrdemServicoId(UUID.randomUUID().toString());
    }

    public static OrdemServicoId de(String valor) {
        return new OrdemServicoId(valor);
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdemServicoId)) return false;
        OrdemServicoId that = (OrdemServicoId) o;
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
