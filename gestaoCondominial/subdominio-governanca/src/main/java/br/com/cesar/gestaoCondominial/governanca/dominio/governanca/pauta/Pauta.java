package br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta;

import br.com.cesar.gestaoCondominial.governanca.dominio.assembleia.AssembleiaId;
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

    public static Pauta reconstituir(
            PautaId id, AssembleiaId assembleiaId, String titulo,
            String descricao, TipoQuorum tipoQuorum, TipoMaioria tipoMaioria,
            StatusPauta status, ResultadoPauta resultado
    ) {
        Pauta pauta = new Pauta();
        pauta.id = id;
        pauta.assembleiaId = assembleiaId;
        pauta.titulo = titulo;
        pauta.descricao = descricao;
        pauta.tipoQuorum = tipoQuorum;
        pauta.tipoMaioria = tipoMaioria;
        pauta.status = status;
        pauta.resultadoFinal = resultado;
        return pauta;
    }

    public static Pauta criar(
            PautaId id,
            AssembleiaId assembleiaId,
            String titulo,
            String descricao,
            TipoQuorum tipoQuorum,
            TipoMaioria tipoMaioria
    ) {

        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título é obrigatório");
        }

        Pauta pauta = new Pauta();
        pauta.id = id;
        pauta.assembleiaId = assembleiaId;
        pauta.titulo = titulo;
        pauta.descricao = descricao;
        pauta.tipoQuorum = tipoQuorum;
        pauta.tipoMaioria = tipoMaioria;
        pauta.status = StatusPauta.ABERTA;

        return pauta;
    }

    public void encerrar(ResultadoPauta resultado) {

        if (this.status == StatusPauta.ENCERRADA) {
            throw new IllegalStateException("Pauta já encerrada");
        }

        this.status = StatusPauta.ENCERRADA;
        this.resultadoFinal = resultado;
    }

    public void validarSeEstaAberta() {
        if (this.status != StatusPauta.ABERTA) {
            throw new IllegalStateException("Pauta não está aberta para votação");
        }
    }

}
