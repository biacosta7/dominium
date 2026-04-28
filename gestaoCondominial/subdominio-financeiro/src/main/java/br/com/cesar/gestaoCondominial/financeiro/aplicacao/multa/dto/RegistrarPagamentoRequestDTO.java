package br.com.cesar.gestaoCondominial.financeiro.aplicacao.multa.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrarPagamentoRequestDTO {

    @NotNull(message = "O valor pago é obrigatório.")
    @DecimalMin(
            value = "0.01",
            message = "O valor pago deve ser maior que zero."
    )
    private BigDecimal valorPago;
}