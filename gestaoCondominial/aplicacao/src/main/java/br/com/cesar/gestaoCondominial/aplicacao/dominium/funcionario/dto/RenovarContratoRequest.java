package br.com.cesar.gestaoCondominial.aplicacao.dominium.funcionario.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RenovarContratoRequest(
        LocalDate novaDataFim,
        BigDecimal novoValorMensal
) {}
