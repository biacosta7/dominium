package br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.repository;

import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.Ocorrencia;
import java.util.Optional;
import java.util.List;

public interface OcorrenciaRepository {
    Ocorrencia salvar(Ocorrencia ocorrencia);
    Optional<Ocorrencia> buscarPorId(Long id);
    List<Ocorrencia> listarTodas();
}