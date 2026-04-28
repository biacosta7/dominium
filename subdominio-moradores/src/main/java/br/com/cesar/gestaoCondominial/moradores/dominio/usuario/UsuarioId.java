package br.com.cesar.gestaoCondominial.moradores.dominio.usuario;

import br.com.cesar.gestaoCondominial.dominio.dominium.valueobjects.ValueObjectId;

public class UsuarioId extends ValueObjectId<Long> {
    public UsuarioId(Long valor) {
        super(valor);
    }
}