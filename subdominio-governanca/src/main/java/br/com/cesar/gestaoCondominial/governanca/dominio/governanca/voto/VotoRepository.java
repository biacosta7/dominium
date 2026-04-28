package br.com.cesar.gestaoCondominial.governanca.dominio.governanca.voto;

import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.moradores.dominio.unidade.UnidadeId;

import java.util.List;
import java.util.Optional;

public interface VotoRepository {

    Voto save(Voto voto);

    Optional<Voto> findById(VotoId votoId);

    List<Voto> buscarPorPauta(PautaId pautaId);

    boolean findByPautaAndUnidade(PautaId pautaId, UnidadeId unidadeId);
}
