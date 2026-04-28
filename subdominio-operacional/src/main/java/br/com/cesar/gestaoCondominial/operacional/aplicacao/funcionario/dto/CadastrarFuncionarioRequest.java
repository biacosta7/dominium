package br.com.cesar.gestaoCondominial.operacional.aplicacao.funcionario.dto;

import br.com.cesar.gestaoCondominial.operacional.dominio.funcionario.TipoVinculo;

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
