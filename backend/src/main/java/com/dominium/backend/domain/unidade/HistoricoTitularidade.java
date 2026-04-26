package com.dominium.backend.domain.unidade;

import java.time.LocalDateTime;

import com.dominium.backend.domain.usuario.Usuario;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class HistoricoTitularidade {
    private Long id;
    private UnidadeId unidadeId;
    private Usuario proprietarioAnterior;
    private Usuario novoProprietario;
    private LocalDateTime dataTransferencia;
}