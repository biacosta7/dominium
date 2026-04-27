package br.com.cesar.gestaoCondominial.dominio.dominium.ocorrencia.repository;

import br.com.cesar.gestaoCondominial.dominio.dominium.ocorrencia.Ocorrencia;
import java.util.Optional;
import java.util.List;

public interface OcorrenciaRepository {
    Ocorrencia salvar(Ocorrencia ocorrencia);
    Optional<Ocorrencia> buscarPorId(Long id);
    List<Ocorrencia> listarTodas();
}