package br.com.cesar.gestaoCondominial.dominio.dominium.funcionario;

import br.com.cesar.gestaoCondominial.dominio.dominium.valueobjects.ValueObjectId;

public class FuncionarioId extends ValueObjectId<Long> {

    public FuncionarioId(Long valor){
        super(valor);
    }

    public static FuncionarioId novo() {
        return new FuncionarioId(null);
    }

    public static FuncionarioId de(String valor) {
        return new FuncionarioId(Long.parseLong(valor));
    }
}
