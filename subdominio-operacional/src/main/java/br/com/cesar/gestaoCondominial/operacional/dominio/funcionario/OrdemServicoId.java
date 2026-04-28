package br.com.cesar.gestaoCondominial.operacional.dominio.funcionario;

import br.com.cesar.gestaoCondominial.dominio.dominium.valueobjects.ValueObjectId;

public class OrdemServicoId extends ValueObjectId<Long> {

    public OrdemServicoId(Long valor){
        super(valor);
    }

    public static OrdemServicoId novo() {
        return new OrdemServicoId(null);
    }

    public static OrdemServicoId de(String valor) {
        return new OrdemServicoId(Long.parseLong(valor));
    }
}