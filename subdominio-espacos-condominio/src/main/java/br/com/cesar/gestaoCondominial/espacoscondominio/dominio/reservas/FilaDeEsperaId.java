package br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas;

import java.util.Objects;

public class FilaDeEsperaId {

    private final String valor;

    public FilaDeEsperaId(String valor) {
        this.valor = valor;
    }

    public FilaDeEsperaId() {
        this.valor = null;
    }

    public String getValor() {
        return valor;
    }

    public static FilaDeEsperaId de(String valor) {
        return new FilaDeEsperaId(valor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilaDeEsperaId that = (FilaDeEsperaId) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return "FilaDeEsperaId(" + valor + ")";
    }
}
