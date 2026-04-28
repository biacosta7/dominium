package br.com.cesar.gestaoCondominial.governanca.aplicacao.governanca.pauta.usecase;

import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.Pauta;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaId;
import br.com.cesar.gestaoCondominial.governanca.dominio.governanca.pauta.PautaRepository;
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
