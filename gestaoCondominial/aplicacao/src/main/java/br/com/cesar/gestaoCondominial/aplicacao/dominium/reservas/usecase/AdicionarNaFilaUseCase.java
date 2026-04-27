package br.com.cesar.gestaoCondominial.aplicacao.dominium.reservas.usecase;

import br.com.cesar.gestaoCondominial.dominio.dominium.areacomum.AreaComum;
import br.com.cesar.gestaoCondominial.dominio.dominium.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.aplicacao.dominium.areacomum.AreaComumService;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.FilaDeEspera;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.FilaDeEsperaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.repository.FilaDeEsperaRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.UsuarioId;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AdicionarNaFilaUseCase {
    private final FilaDeEsperaRepository repository;
    private final AreaComumService areaComumService;

    public AdicionarNaFilaUseCase(FilaDeEsperaRepository repository, AreaComumService areaComumService) {
        this.repository = repository;
        this.areaComumService = areaComumService;
    }

    public FilaDeEspera executar(AreaComumId areaId, Long usuarioId, LocalDate data, LocalTime inicio, LocalTime fim) {

        AreaComum area = areaComumService.buscarArea(areaId);

        FilaDeEspera fila = FilaDeEspera.criar(new FilaDeEsperaId(), areaId, new UsuarioId(usuarioId), data, inicio, fim);

        return repository.salvar(fila);
    }
}