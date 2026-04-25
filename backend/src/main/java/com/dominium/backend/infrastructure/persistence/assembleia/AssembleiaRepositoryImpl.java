package com.dominium.backend.infrastructure.persistence.assembleia;

import com.dominium.backend.domain.assembleia.Assembleia;
import com.dominium.backend.domain.assembleia.AssembleiaId;
import com.dominium.backend.domain.assembleia.StatusAssembleia;
import com.dominium.backend.domain.assembleia.repository.AssembleiaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AssembleiaRepositoryImpl implements AssembleiaRepository {

    private final JdbcTemplate jdbcTemplate;

    public AssembleiaRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Assembleia save(Assembleia assembleia) {
        boolean existe = Boolean.TRUE.equals(
            jdbcTemplate.queryForObject(
                "SELECT COUNT(*) > 0 FROM assembleias WHERE id = ?",
                Boolean.class,
                assembleia.getId().getValor()
            )
        );

        if (!existe) {
            jdbcTemplate.update(
                "INSERT INTO assembleias (id, titulo, data_hora, local, status, sindico_id, data_criacao) VALUES (?, ?, ?, ?, ?, ?, ?)",
                assembleia.getId().getValor(),
                assembleia.getTitulo(),
                Timestamp.valueOf(assembleia.getDataHora()),
                assembleia.getLocal(),
                assembleia.getStatus().name(),
                assembleia.getSindicoId(),
                Timestamp.valueOf(assembleia.getDataCriacao())
            );
            salvarPauta(assembleia);
        } else {
            jdbcTemplate.update(
                "UPDATE assembleias SET titulo = ?, data_hora = ?, local = ?, status = ? WHERE id = ?",
                assembleia.getTitulo(),
                Timestamp.valueOf(assembleia.getDataHora()),
                assembleia.getLocal(),
                assembleia.getStatus().name(),
                assembleia.getId().getValor()
            );
            jdbcTemplate.update("DELETE FROM pauta WHERE assembleia_id = ?", assembleia.getId().getValor());
            salvarPauta(assembleia);
        }

        return assembleia;
    }

    @Override
    public Optional<Assembleia> findById(AssembleiaId id) {
        List<Assembleia> results = jdbcTemplate.query(
            "SELECT * FROM assembleias WHERE id = ?",
            (rs, rowNum) -> Assembleia.reconstituir(
                new AssembleiaId(rs.getLong("id")),
                rs.getString("titulo"),
                rs.getTimestamp("data_hora").toLocalDateTime(),
                rs.getString("local"),
                new ArrayList<>(),
                StatusAssembleia.valueOf(rs.getString("status")),
                rs.getLong("sindico_id"),
                rs.getTimestamp("data_criacao").toLocalDateTime()
            ),
            id.getValor()
        );

        if (results.isEmpty()) return Optional.empty();

        Assembleia assembleia = results.get(0);
        assembleia.setPauta(buscarPauta(assembleia.getId()));
        return Optional.of(assembleia);
    }

    @Override
    public List<Assembleia> findAll() {
        List<Assembleia> assembleias = jdbcTemplate.query(
            "SELECT * FROM assembleias ORDER BY data_hora",
            (rs, rowNum) -> Assembleia.reconstituir(
                new AssembleiaId(rs.getLong("id")),
                rs.getString("titulo"),
                rs.getTimestamp("data_hora").toLocalDateTime(),
                rs.getString("local"),
                new ArrayList<>(),
                StatusAssembleia.valueOf(rs.getString("status")),
                rs.getLong("sindico_id"),
                rs.getTimestamp("data_criacao").toLocalDateTime()
            )
        );

        assembleias.forEach(a -> a.setPauta(buscarPauta(a.getId())));
        return assembleias;
    }

    private void salvarPauta(Assembleia assembleia) {
        if (assembleia.getPauta() == null) return;
        for (String item : assembleia.getPauta()) {
            jdbcTemplate.update(
                "INSERT INTO pauta (assembleia_id, descricao) VALUES (?, ?)",
                assembleia.getId().getValor(),
                item
            );
        }
    }

    private List<String> buscarPauta(AssembleiaId id) {
        return jdbcTemplate.query(
            "SELECT descricao FROM pauta WHERE assembleia_id = ? ORDER BY id",
            (rs, rowNum) -> rs.getString("descricao"),
            id.getValor()
        );
    }
}
