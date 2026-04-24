package com.dominium.backend.domain.governanca.pauta;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Pauta {

    private PautaId id;
    private AssembleiaId assembleiaId;
    private String titulo;
    private String descricao;
    private TipoQuorum tipoQuorum;
    private TipoMaioria tipoMaioria;
    private StatusPauta status;
    private ResultadoPauta resultadoFinal;
}
