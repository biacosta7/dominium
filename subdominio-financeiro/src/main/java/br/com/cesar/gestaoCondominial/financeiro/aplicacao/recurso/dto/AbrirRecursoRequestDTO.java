package br.com.cesar.gestaoCondominial.financeiro.aplicacao.recurso.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AbrirRecursoRequestDTO {
    private Long multaId;
    private Long usuarioId;

    // Mantido para compatibilidade com os cenarios da primeira entrega.
    private UUID moradorId;
    private String motivo;
}
