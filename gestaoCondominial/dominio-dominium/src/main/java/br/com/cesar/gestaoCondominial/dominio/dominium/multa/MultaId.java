package br.com.cesar.gestaoCondominial.dominio.dominium.multa;

import br.com.cesar.gestaoCondominial.dominio.dominium.valueobjects.ValueObjectId;

public class MultaId extends ValueObjectId<Long> {
    public MultaId(Long valor){
        super(valor);
    }
}
