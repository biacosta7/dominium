package com.dominium.backend.infrastructure.persistence.unidade;

import com.dominium.backend.domain.unidade.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUnidadeRepository extends JpaRepository<Unidade, Long> {
    Optional<Unidade> findByNumeroAndBloco(String numero, String bloco);
}