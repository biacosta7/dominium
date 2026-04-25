package com.dominium.backend.infrastructure.persistence.multa;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.dominium.backend.domain.multa.Multa;
import com.dominium.backend.domain.multa.StatusMulta;
import com.dominium.backend.domain.multa.TipoValorMulta;
import com.dominium.backend.domain.multa.repository.MultaRepository;
import com.dominium.backend.domain.unidade.repository.UnidadeRepository;

@Repository
public class MultaRepositoryImpl implements MultaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UnidadeRepository unidadeRepository;

    public MultaRepositoryImpl(
            JdbcTemplate jdbcTemplate,
            UnidadeRepository unidadeRepository
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.unidadeRepository = unidadeRepository;
    }

    private final RowMapper<Multa> rowMapper = new RowMapper<>() {
        @Override
        public Multa mapRow(ResultSet rs, int rowNum) throws SQLException {
            Multa multa = new Multa();

            multa.setId(rs.getLong("id"));
            multa.setOcorrenciaId(rs.getObject("ocorrencia_id", Long.class));

            multa.setUnidade(
                    unidadeRepository.findById(rs.getLong("unidade_id"))
                            .orElse(null)
            );

            multa.setDescricao(rs.getString("descricao"));
            multa.setValor(rs.getBigDecimal("valor"));
            multa.setValorBase(rs.getBigDecimal("valor_base"));

            multa.setTipoValor(
                    TipoValorMulta.valueOf(rs.getString("tipo_valor"))
            );

            multa.setStatus(
                    StatusMulta.valueOf(rs.getString("status"))
            );

            multa.setReincidencia(rs.getInt("reincidencia"));

            if (rs.getTimestamp("data_criacao") != null) {
                multa.setDataCriacao(
                        rs.getTimestamp("data_criacao").toLocalDateTime()
                );
            }

            if (rs.getTimestamp("data_pagamento") != null) {
                multa.setDataPagamento(
                        rs.getTimestamp("data_pagamento").toLocalDateTime()
                );
            }

            multa.setValorPago(rs.getBigDecimal("valor_pago"));

            if (rs.getTimestamp("updated_at") != null) {
                multa.setUpdatedAt(
                        rs.getTimestamp("updated_at").toLocalDateTime()
                );
            }
            multa.setJustificativaContestacao(
                rs.getString("justificativa_contestacao")
            );
            
            if (rs.getTimestamp("data_contestacao") != null) {
                multa.setDataContestacao(
                        rs.getTimestamp("data_contestacao")
                                .toLocalDateTime()
                );
            }
            return multa;
        }   
    };

    @Override
    public Multa save(Multa multa) {
        if (multa.getId() == null) {
            return insert(multa);
        }

        return update(multa);
    }

    private Multa insert(Multa multa) {
        String sql = """
            INSERT INTO multas (
                ocorrencia_id,
                unidade_id,
                descricao,
                valor,
                valor_base,
                tipo_valor,
                status,
                reincidencia,
                data_criacao,
                data_pagamento,
                valor_pago,
                updated_at,
                justificativa_contestacao,
                data_contestacao
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS
            );

            if (multa.getOcorrenciaId() != null) {
                ps.setLong(1, multa.getOcorrenciaId());
            } else {
                ps.setNull(1, Types.BIGINT);
            }

            ps.setLong(2, multa.getUnidade().getId());
            ps.setString(3, multa.getDescricao());
            ps.setBigDecimal(4, multa.getValor());
            ps.setBigDecimal(5, multa.getValorBase());
            ps.setString(6, multa.getTipoValor().name());
            ps.setString(7, multa.getStatus().name());
            ps.setInt(8, multa.getReincidencia());
            ps.setTimestamp(9, java.sql.Timestamp.valueOf(multa.getDataCriacao()));

            if (multa.getDataPagamento() != null) {
                ps.setTimestamp(10,
                        java.sql.Timestamp.valueOf(multa.getDataPagamento()));
            } else {
                ps.setNull(10, Types.TIMESTAMP);
            }

            ps.setBigDecimal(11, multa.getValorPago());

            if (multa.getUpdatedAt() != null) {
                ps.setTimestamp(12,
                        java.sql.Timestamp.valueOf(multa.getUpdatedAt()));
            } else {
                ps.setNull(12, Types.TIMESTAMP);
            }

            ps.setString(13, multa.getJustificativaContestacao());

            if (multa.getDataContestacao() != null) {
                ps.setTimestamp(
                        14,
                        java.sql.Timestamp.valueOf(
                                multa.getDataContestacao()
                        )
                );
            } else {
                ps.setNull(14, Types.TIMESTAMP);
            }

            return ps;
        }, keyHolder);

        multa.setId(keyHolder.getKey().longValue());
        return multa;
    }

    private Multa update(Multa multa) {
        String sql = """
            UPDATE multas
               SET ocorrencia_id = ?,
                   unidade_id = ?,
                   descricao = ?,
                   valor = ?,
                   valor_base = ?,
                   tipo_valor = ?,
                   status = ?,
                   reincidencia = ?,
                   data_pagamento = ?,
                   valor_pago = ?,
                   updated_at = ?,
                   justificativa_contestacao = ?,
                   data_contestacao = ?
             WHERE id = ?
            """;

        jdbcTemplate.update(
                sql,
                multa.getOcorrenciaId(),
                multa.getUnidade().getId(),
                multa.getDescricao(),
                multa.getValor(),
                multa.getValorBase(),
                multa.getTipoValor().name(),
                multa.getStatus().name(),
                multa.getReincidencia(),
                multa.getDataPagamento(),
                multa.getValorPago(),
                multa.getUpdatedAt(),
                multa.getJustificativaContestacao(),
                multa.getDataContestacao(),
                multa.getId()
        );

        return multa;
    }

    @Override
    public Optional<Multa> findById(Long id) {
        List<Multa> multas = jdbcTemplate.query(
                "SELECT * FROM multas WHERE id = ?",
                rowMapper,
                id
        );
        return multas.stream().findFirst();
    }

    @Override
    public List<Multa> findAll() {
        return jdbcTemplate.query(
                "SELECT * FROM multas ORDER BY data_criacao DESC",
                rowMapper
        );
    }

    @Override
    public List<Multa> findByUnidadeId(Long unidadeId) {
        return jdbcTemplate.query(
                "SELECT * FROM multas WHERE unidade_id = ? ORDER BY data_criacao DESC",
                rowMapper,
                unidadeId
        );
    }

    @Override
    public List<Multa> findByUnidadeIdAndStatus(
            Long unidadeId,
            StatusMulta status
    ) {
        return jdbcTemplate.query(
                """
                SELECT * FROM multas
                 WHERE unidade_id = ?
                   AND status = ?
                 ORDER BY data_criacao DESC
                """,
                rowMapper,
                unidadeId,
                status.name()
        );
    }

    @Override
    public List<Multa> findByOcorrenciaId(Long ocorrenciaId) {
        return jdbcTemplate.query(
                "SELECT * FROM multas WHERE ocorrencia_id = ?",
                rowMapper,
                ocorrenciaId
        );
    }

    @Override
    public long countByUnidadeIdAndDescricao(
            Long unidadeId,
            String descricao
    ) {
        Long count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                  FROM multas
                 WHERE unidade_id = ?
                   AND descricao = ?
                """,
                Long.class,
                unidadeId,
                descricao
        );

        return count != null ? count : 0L;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(
                "DELETE FROM multas WHERE id = ?",
                id
        );
    }
}