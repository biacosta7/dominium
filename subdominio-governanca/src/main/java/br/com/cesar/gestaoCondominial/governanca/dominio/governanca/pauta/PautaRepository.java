package br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta;

import java.util.List;
import java.util.Optional;

public interface PautaRepository {

    Pauta save(Pauta pauta);

    Optional<Pauta> findById(PautaId pautaId);

    List<Pauta> buscarAbertas();

}
