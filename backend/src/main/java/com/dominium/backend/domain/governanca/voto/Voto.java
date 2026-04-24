package com.dominium.backend.domain.governanca.voto;

import com.dominium.backend.domain.governanca.pauta.PautaId;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.usuario.UsuarioId;
import lombok.*;

import java.time.DateTimeException;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Voto {

    private VotoId id;
    private PautaId pautaId;
    private UnidadeId unidadeId;
    private UsuarioId usuarioId;
    private OpcaoVoto opcaoVoto;
    private Date dataHora;
}
