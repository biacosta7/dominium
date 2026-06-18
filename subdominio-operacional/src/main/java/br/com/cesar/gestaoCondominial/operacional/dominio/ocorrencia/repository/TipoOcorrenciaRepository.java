package br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.repository;

import br.com.cesar.gestaoCondominial.operacional.dominio.ocorrencia.TipoOcorrencia;

import java.util.List;
import java.util.Optional;

public interface TipoOcorrenciaRepository {
    List<TipoOcorrencia> findAll();
    Optional<TipoOcorrencia> findByNome(String nome);
}
