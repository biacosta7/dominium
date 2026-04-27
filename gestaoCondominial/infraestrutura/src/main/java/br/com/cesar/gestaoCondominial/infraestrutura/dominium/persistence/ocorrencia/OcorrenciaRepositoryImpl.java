package com.dominium.backend.infrastructure.persistence.ocorrencia;

import com.dominium.backend.domain.ocorrencia.Ocorrencia;
import com.dominium.backend.domain.ocorrencia.Ocorrencia.StatusOcorrencia;
import com.dominium.backend.domain.ocorrencia.TipoPenalidade;
import com.dominium.backend.domain.ocorrencia.repository.OcorrenciaRepository;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class OcorrenciaRepositoryImpl implements OcorrenciaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UsuarioRepository usuarioRepository;

    public OcorrenciaRepositoryImpl(JdbcTemplate jdbcTemplate, UsuarioRepository usuarioRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.usuarioRepository = usuarioRepository;
    }

    private RowMapper<Ocorrencia> getRowMapper() {
        return (rs, rowNum) -> {
            Ocorrencia ocorrencia = new Ocorrencia();
            ocorrencia.setId(rs.getLong("id"));
            ocorrencia.setDescricao(rs.getString("descricao"));
            ocorrencia.setUnidadeId(new UnidadeId(rs.getLong("unidade_id")));
            
            Long usuarioId = rs.getLong("usuario_id");
            if (usuarioId != null && usuarioId > 0) {
                Usuario relator = usuarioRepository.findById(usuarioId).orElse(null);
                ocorrencia.setRelator(relator);
            }

            if (rs.getTimestamp("data_registro") != null) {
                ocorrencia.setDataRegistro(rs.getTimestamp("data_registro").toLocalDateTime());
            }

            ocorrencia.setStatus(StatusOcorrencia.valueOf(rs.getString("status")));

            String penalidadeStr = rs.getString("penalidade");
            if (penalidadeStr != null) {
                ocorrencia.setPenalidade(TipoPenalidade.valueOf(penalidadeStr));
            }

            ocorrencia.setObservacaoSindico(rs.getString("observacao_sindico"));

            return ocorrencia;
        };
    }

    @Override
    public Ocorrencia salvar(Ocorrencia ocorrencia) {
        if (ocorrencia.getId() == null) {
            return insert(ocorrencia);
        }
        return update(ocorrencia);
    }

    private Ocorrencia insert(Ocorrencia ocorrencia) {
        String sql = """
            INSERT INTO ocorrencias (
                descricao,
                unidade_id,
                usuario_id,
                data_registro,
                status,
                penalidade,
                observacao_sindico
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, ocorrencia.getDescricao());
            ps.setLong(2, ocorrencia.getUnidadeId().getValor());
            
            if (ocorrencia.getRelator() != null && ocorrencia.getRelator().getId() != null) {
                ps.setLong(3, ocorrencia.getRelator().getId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            
            if (ocorrencia.getDataRegistro() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(ocorrencia.getDataRegistro()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }
            
            ps.setString(5, ocorrencia.getStatus().name());
            
            if (ocorrencia.getPenalidade() != null) {
                ps.setString(6, ocorrencia.getPenalidade().name());
            } else {
                ps.setNull(6, Types.VARCHAR);
            }
            
            ps.setString(7, ocorrencia.getObservacaoSindico());

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            ocorrencia.setId(keyHolder.getKey().longValue());
        }
        return ocorrencia;
    }

    private Ocorrencia update(Ocorrencia ocorrencia) {
        String sql = """
            UPDATE ocorrencias
               SET descricao = ?,
                   unidade_id = ?,
                   usuario_id = ?,
                   data_registro = ?,
                   status = ?,
                   penalidade = ?,
                   observacao_sindico = ?
             WHERE id = ?
            """;

        jdbcTemplate.update(
                sql,
                ocorrencia.getDescricao(),
                ocorrencia.getUnidadeId() != null ? ocorrencia.getUnidadeId().getValor() : null,
                ocorrencia.getRelator() != null ? ocorrencia.getRelator().getId() : null,
                ocorrencia.getDataRegistro() != null ? Timestamp.valueOf(ocorrencia.getDataRegistro()) : null,
                ocorrencia.getStatus() != null ? ocorrencia.getStatus().name() : null,
                ocorrencia.getPenalidade() != null ? ocorrencia.getPenalidade().name() : null,
                ocorrencia.getObservacaoSindico(),
                ocorrencia.getId()
        );

        return ocorrencia;
    }

    @Override
    public Optional<Ocorrencia> buscarPorId(Long id) {
        List<Ocorrencia> ocorrencias = jdbcTemplate.query(
                "SELECT * FROM ocorrencias WHERE id = ?",
                getRowMapper(),
                id
        );
        return ocorrencias.stream().findFirst();
    }

    @Override
    public List<Ocorrencia> listarTodas() {
        return jdbcTemplate.query(
                "SELECT * FROM ocorrencias ORDER BY data_registro DESC",
                getRowMapper()
        );
    }
}
