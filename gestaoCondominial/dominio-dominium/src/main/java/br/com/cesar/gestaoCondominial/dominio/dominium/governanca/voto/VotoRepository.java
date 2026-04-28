package br.com.cesar.gestaoCondominial.dominio.dominium.governanca.voto;

import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;

import java.util.List;
import java.util.Optional;

public interface VotoRepository {

    Voto save(Voto voto);

    Optional<Voto> findById(VotoId votoId);

    List<Voto> buscarPorPauta(PautaId pautaId);

    boolean findByPautaAndUnidade(PautaId pautaId, UnidadeId unidadeId);
}
