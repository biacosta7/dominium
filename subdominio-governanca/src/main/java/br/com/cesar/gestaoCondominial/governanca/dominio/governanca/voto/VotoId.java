package br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto;

import br.com.cesar.gestaoCondominial.dominio.dominium.valueobjects.ValueObjectId;

public class VotoId extends ValueObjectId<Long> {

    public VotoId(Long valor){
        super(valor);
    }
}
