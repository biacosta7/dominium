package com.dominium.backend.domain.morador.repository;

import java.util.List;
import java.util.Optional;

import com.dominium.backend.domain.morador.StatusVinculo;
import com.dominium.backend.domain.morador.VinculoMorador;

public interface VinculoMoradorRepository {
    VinculoMorador save(VinculoMorador vinculo);
    Optional<VinculoMorador> findById(Long id);
    List<VinculoMorador> findByUnidadeIdAndStatus(Long unidadeId, StatusVinculo status);
    List<VinculoMorador> findByUsuarioIdAndStatus(Long usuarioId, StatusVinculo status);
    List<VinculoMorador> findByUsuarioAndUnidade(Long usuarioId, Long unidadeId);
    long countByUnidadeIdAndStatus(Long unidadeId, StatusVinculo status);
    void deleteById(Long id);
}
