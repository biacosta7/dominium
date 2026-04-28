package br.com.cesar.gestaoCondominial.aplicacao.dominium.multa.dto;

import java.math.BigDecimal;

import br.com.cesar.gestaoCondominial.dominio.dominium.multa.TipoValorMulta;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMultaRequestDTO {

    private Long ocorrenciaId;

    @NotNull(message = "O ID da unidade é obrigatório.")
    private Long unidadeId;

    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    private BigDecimal valor;

    @NotNull(message = "O tipo de valor é obrigatório.")
    private TipoValorMulta tipoValor;
}