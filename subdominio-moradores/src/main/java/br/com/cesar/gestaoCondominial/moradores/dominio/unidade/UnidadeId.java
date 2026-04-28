package br.com.cesar.gestaoCondominial.moradores.dominio.unidade;

import br.com.cesar.gestaoCondominial.dominio.dominium.valueobjects.ValueObjectId;

public class UnidadeId extends ValueObjectId<Long> {
    public UnidadeId(Long valor) {
        super(valor);
    }
}
