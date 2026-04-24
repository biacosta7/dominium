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

    public static Voto criar(
            VotoId id,
            PautaId pautaId,
            UnidadeId unidadeId,
            UsuarioId usuarioId,
            OpcaoVoto opcao
    ) {

        if (opcao == null) {
            throw new IllegalArgumentException("Opção de voto é obrigatória");
        }

        Voto voto = new Voto();
        voto.id = id;
        voto.pautaId = pautaId;
        voto.unidadeId = unidadeId;
        voto.usuarioId = usuarioId;
        voto.opcaoVoto = opcao;
        voto.dataHora = new Date();

        return voto;
    }

    public boolean pertenceAPauta(PautaId pautaId) {
        return this.pautaId.equals(pautaId);
    }

    public boolean ehDaUnidade(UnidadeId unidadeId) {
        return this.unidadeId.equals(unidadeId);
    }

}
