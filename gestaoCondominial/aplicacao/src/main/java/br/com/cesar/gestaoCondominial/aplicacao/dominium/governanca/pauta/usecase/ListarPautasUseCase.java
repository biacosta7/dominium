package br.com.cesar.gestaoCondominial.aplicacao.dominium.governanca.pauta.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.governanca.pauta.PautaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarPautasUseCase {
    private final PautaRepository repository;

    public ListarPautasUseCase(PautaRepository repository) {
        this.repository = repository;
    }

    public List<Pauta> executar() {
        return repository.buscarAbertas();
    }
}
