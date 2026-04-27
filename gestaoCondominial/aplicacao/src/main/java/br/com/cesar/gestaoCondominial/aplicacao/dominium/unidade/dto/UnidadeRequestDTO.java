package br.com.cesar.gestaoCondominial.aplicacao.dominium.unidade.dto;

import java.math.BigDecimal;

import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.StatusAdimplencia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnidadeRequestDTO {

    @NotBlank(message = "O número da unidade é obrigatório")
    private String numero;

    @NotBlank(message = "O bloco é obrigatório")
    private String bloco;

    @NotNull(message = "O proprietário é obrigatório")
    private Long proprietarioId;

    private Long inquilinoId;

    @NotNull(message = "O status de adimplência é obrigatório")
    private StatusAdimplencia status;

    @NotNull(message = "O saldo devedor é obrigatório")
    private BigDecimal saldoDevedor;
}