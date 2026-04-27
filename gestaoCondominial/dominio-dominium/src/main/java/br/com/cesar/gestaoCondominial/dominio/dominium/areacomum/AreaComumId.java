package br.com.cesar.gestaoCondominial.dominio.dominium.areacomum;

import br.com.cesar.gestaoCondominial.dominio.compartilhado.valueobjects.ValueObjectId;
//import br.com.cesar.gestaoCondominial.dominio.compartilhado.exceptions.DomainException;

public class AreaComumId extends ValueObjectId<Long> {
    public AreaComumId(Long valor) {
        super(valor);
    }
}
