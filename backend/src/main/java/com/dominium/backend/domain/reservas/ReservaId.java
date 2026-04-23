package com.dominium.backend.domain.reservas;

import java.util.Objects;
import java.util.UUID;

public class ReservaId {

    private final String valor;

    // Construtor privado (força uso dos métodos de criação)
    private ReservaId(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("ID da reserva não pode ser vazio");
        }
        this.valor = valor;
    }

    // 🔥 Criação de novo ID (para novas reservas)
    public static ReservaId novo() {
        return new ReservaId(UUID.randomUUID().toString());
    }

    // 🔄 Recriar ID vindo do banco
    public static ReservaId de(String valor) {
        return new ReservaId(valor);
    }

    public String getValor() {
        return valor;
    }

    // equals e hashCode (IMPORTANTE em Value Object)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReservaId)) return false;
        ReservaId that = (ReservaId) o;
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
