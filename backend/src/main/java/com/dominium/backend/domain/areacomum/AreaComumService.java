package com.dominium.backend.domain.areacomum;

import com.dominium.backend.domain.areacomum.AreaComum;
import com.dominium.backend.domain.areacomum.AreaComumRepository;

public class AreaComumService {

    private final AreaComumRepository repository;

    public AreaComumService(AreaComumRepository repository){
        this.repository = repository;
    }

    public AreaComum buscarArea(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Área comum não encontrada"));
    }

    public void validarDisponibilidade(AreaComum area) {
        if (!area.estaDisponivel()) {
            throw new RuntimeException("Área não está disponível");
        }
    }

}
