package com.dominium.backend.application.funcionario.dto;

import com.dominium.backend.domain.funcionario.Funcionario;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FuncionarioResponse(
        String id,
        String nome,
        String cpf,
        String email,
        String telefone,
        String tipoVinculo,
        String status,
        LocalDate contratoInicio,
        LocalDate contratoFim,
        BigDecimal valorMensal
) {
    public static FuncionarioResponse from(Funcionario f) {
        return new FuncionarioResponse(
                f.getId().getValor(),
                f.getNome(),
                f.getCpf(),
                f.getEmail(),
                f.getTelefone(),
                f.getTipoVinculo().name(),
                f.getStatus().name(),
                f.getContratoInicio(),
                f.getContratoFim(),
                f.getValorMensal()
        );
    }
}
