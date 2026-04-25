package com.dominium.backend.application.funcionario.dto;

import com.dominium.backend.domain.funcionario.TipoVinculo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CadastrarFuncionarioRequest(
        String nome,
        String cpf,
        String email,
        String telefone,
        TipoVinculo tipoVinculo,
        LocalDate contratoInicio,
        LocalDate contratoFim,
        BigDecimal valorMensal
) {}
