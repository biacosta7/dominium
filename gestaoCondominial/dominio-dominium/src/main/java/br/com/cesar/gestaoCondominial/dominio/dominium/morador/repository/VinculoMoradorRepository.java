package br.com.cesar.gestaoCondominial.dominio.dominium.morador.repository;

import java.util.List;
import java.util.Optional;

import br.com.cesar.gestaoCondominial.dominio.dominium.morador.VinculoMorador;
import br.com.cesar.gestaoCondominial.dominio.dominium.morador.StatusVinculo;

public interface VinculoMoradorRepository {
    VinculoMorador save(VinculoMorador vinculo);
    Optional<VinculoMorador> findById(Long id);
    List<VinculoMorador> findByUnidadeIdAndStatus(Long unidadeId, StatusVinculo status);
    List<VinculoMorador> findByUsuarioIdAndStatus(Long usuarioId, StatusVinculo status);
    List<VinculoMorador> findByUsuarioAndUnidade(Long usuarioId, Long unidadeId);
    long countByUnidadeIdAndStatus(Long unidadeId, StatusVinculo status);
    void deleteById(Long id);
}
