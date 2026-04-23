package com.dominium.backend.domain.areacomum;

public class AreaComumId {

    private final Long valor;

    public AreaComumId(Long valor) {
        if (valor == null) {
            throw new IllegalArgumentException("ID inválido");
        }
        this.valor = valor;
    }

    public Long getValor() {
        return valor;
    }
}
