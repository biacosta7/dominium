package br.com.cesar.gestaoCondominial.aplicacao.dominium.funcionario.dto;

import br.com.cesar.gestaoCondominial.dominio.dominium.funcionario.TipoVinculo;

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
