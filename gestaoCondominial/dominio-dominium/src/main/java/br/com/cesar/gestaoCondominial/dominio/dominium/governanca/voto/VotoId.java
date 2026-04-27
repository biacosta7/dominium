package br.com.cesar.gestaoCondominial.dominio.dominium.governanca.voto;

import com.dominium.backend.domain.shared.valueobjects.ValueObjectId;

public class VotoId extends ValueObjectId<Long> {

    public VotoId(Long valor){
        super(valor);
    }
}
