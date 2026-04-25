package com.dominium.backend.infrastructure.persistence.assembleia;

import com.dominium.backend.domain.assembleia.Assembleia;
import com.dominium.backend.domain.assembleia.AssembleiaId;
import com.dominium.backend.domain.assembleia.Pauta;
import com.dominium.backend.domain.assembleia.repository.AssembleiaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class AssembleiaRepositoryImpl implements AssembleiaRepository {

    private final JdbcTemplate jdbcTemplate;

    public AssembleiaRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void salvar(Assembleia assembleia) {
        String sqlAssembleia = "INSERT INTO assembleia (id, data_hora, local, concluida) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlAssembleia,
                assembleia.getId().getId(),
                assembleia.getDataHora(),
                assembleia.getLocal(),
                assembleia.isConcluida());

        String sqlPauta = "INSERT INTO pauta (id, assembleia_id, titulo, descricao, votos_sim, votos_nao, abstencoes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        assembleia.getPautas().forEach(pauta -> {
            jdbcTemplate.update(sqlPauta,
                    pauta.getId(),
                    assembleia.getId().getId(),
                    pauta.getTitulo(),
                    pauta.getDescricao(),
                    pauta.getVotosSim(),
                    pauta.getVotosNao(),
                    pauta.getAbstencoes());
        });
    }

    @Override
    public Optional<Assembleia> buscarPorId(AssembleiaId id) {
        return Optional.empty();
    }

    @Override
    public void registrarVoto(Pauta pauta, String unidadeId, String tipoVoto) {
        String sql = "UPDATE pauta SET votos_sim = ?, votos_nao = ?, abstencoes = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                pauta.getVotosSim(),
                pauta.getVotosNao(),
                pauta.getAbstencoes(),
                pauta.getId());
    }
}