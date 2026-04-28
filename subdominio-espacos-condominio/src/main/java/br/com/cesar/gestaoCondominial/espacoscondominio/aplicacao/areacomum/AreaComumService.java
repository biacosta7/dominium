package br.com.cesar.gestaoCondominial.espacoscondominio.aplicacao.areacomum;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComum;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumRepository;
import org.springframework.stereotype.Service;

@Service
public class AreaComumService {

    private final AreaComumRepository repository;

    public AreaComumService(AreaComumRepository repository){
        this.repository = repository;
    }

    public AreaComum buscarArea(AreaComumId id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área comum não encontrada"));
    }

    public void validarDisponibilidade(AreaComum area) {
        if (!area.estaDisponivel()) {
            throw new RuntimeException("Área não está disponível");
        }
    }

}
