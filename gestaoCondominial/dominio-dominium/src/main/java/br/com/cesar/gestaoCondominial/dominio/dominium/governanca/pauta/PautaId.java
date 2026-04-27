package br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta;

import com.dominium.backend.domain.shared.valueobjects.ValueObjectId;

public class PautaId extends ValueObjectId<Long> {

    public PautaId(Long valor) {
        super(valor);
    }
}
