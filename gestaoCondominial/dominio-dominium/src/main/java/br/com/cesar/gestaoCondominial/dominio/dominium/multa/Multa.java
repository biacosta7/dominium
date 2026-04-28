package br.com.cesar.gestaoCondominial.dominio.dominium.multa;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.Unidade;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Multa {
    private MultaId id;

    // Pode ser null (multa manual)
    private Long ocorrenciaId;
    private Unidade unidade;

    private String descricao;

    private BigDecimal valor;

    private TipoValorMulta tipoValor; // FIXO ou PERCENTUAL

    private StatusMulta status;

    private BigDecimal valorBase; // ex: taxa mensal (se percentual)

    private Integer reincidencia; // quantas vezes ocorreu

    private LocalDateTime dataCriacao;

    private LocalDateTime dataPagamento;

    private BigDecimal valorPago;

    private LocalDateTime updatedAt;
    // Novos campos na entidade Multa

    private String justificativaContestacao;

    private LocalDateTime dataContestacao;
}