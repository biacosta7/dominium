package br.com.cesar.gestaoCondominial.dominio.dominium.multa;

import com.dominium.backend.domain.shared.valueobjects.ValueObjectId;

public class MultaId extends ValueObjectId<Long> {
    public MultaId(Long valor){
        super(valor);
    }
}
