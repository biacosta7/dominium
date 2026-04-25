package com.dominium.backend.domain.documento;

import java.util.Objects;
import java.util.UUID;

public class DocumentoId {

    private final String valor;

    private DocumentoId(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("ID do documento não pode ser vazio");
        }
        this.valor = valor;
    }

    public static DocumentoId novo() {
        return new DocumentoId(UUID.randomUUID().toString());
    }

    public static DocumentoId de(String valor) {
        return new DocumentoId(valor);
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DocumentoId)) return false;
        DocumentoId that = (DocumentoId) o;
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
