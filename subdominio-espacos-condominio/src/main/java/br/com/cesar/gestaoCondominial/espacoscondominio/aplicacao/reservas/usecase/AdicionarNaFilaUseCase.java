package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.reservas.usecase;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComum;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.areacomum.AreaComumService;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.FilaDeEspera;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.FilaDeEsperaId;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.reservas.repository.FilaDeEsperaRepository;
import br.com.cesar.gestaoCondominial.moradores.dominio.usuario.UsuarioId;
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