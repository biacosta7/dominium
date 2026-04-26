package com.dominium.backend.infrastructure.persistence.taxa;

import com.dominium.backend.domain.taxa.StatusTaxa;
import com.dominium.backend.domain.taxa.TaxaCondominial;
import com.dominium.backend.domain.taxa.TaxaId;
import com.dominium.backend.domain.taxa.repository.TaxaCondominialRepository;
import com.dominium.backend.domain.unidade.UnidadeId;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTaxaCondominialRepository implements TaxaCondominialRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTaxaCondominialRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<TaxaCondominial> rowMapper = (rs, rowNum) -> new TaxaCondominial(
            new TaxaId(rs.getLong("id")),
            new UnidadeId(rs.getLong("unidade_id")),
            rs.getBigDecimal("valor_base"),
            rs.getBigDecimal("valor_multas"),
            rs.getBigDecimal("valor_total"),
            rs.getDate("data_vencimento") != null ? rs.getDate("data_vencimento").toLocalDate() : null,
            rs.getTimestamp("data_pagamento") != null ? rs.getTimestamp("data_pagamento").toLocalDateTime() : null,
            StatusTaxa.valueOf(rs.getString("status"))
    );

    @Override
    public void salvar(TaxaCondominial taxa) {
        String sql = "INSERT INTO taxa_condominial (unidade_id, valor_base, valor_multas, valor_total, data_vencimento, data_pagamento, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, taxa.getUnidadeId().getValor());
            ps.setBigDecimal(2, taxa.getValorBase());
            ps.setBigDecimal(3, taxa.getValorMultas());
            ps.setBigDecimal(4, taxa.getValorTotal());
            ps.setDate(5, java.sql.Date.valueOf(taxa.getDataVencimento()));
            ps.setTimestamp(6, taxa.getDataPagamento() != null ? java.sql.Timestamp.valueOf(taxa.getDataPagamento()) : null);
            ps.setString(7, taxa.getStatus().name());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            taxa.setId(new TaxaId(keyHolder.getKey().longValue()));
        }
    }

    @Override
    public void atualizar(TaxaCondominial taxa) {
        String sql = "UPDATE taxa_condominial SET valor_base = ?, valor_multas = ?, valor_total = ?, data_vencimento = ?, data_pagamento = ?, status = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                taxa.getValorBase(),
                taxa.getValorMultas(),
                taxa.getValorTotal(),
                taxa.getDataVencimento() != null ? java.sql.Date.valueOf(taxa.getDataVencimento()) : null,
                taxa.getDataPagamento() != null ? java.sql.Timestamp.valueOf(taxa.getDataPagamento()) : null,
                taxa.getStatus().name(),
                taxa.getId().getValor()
        );
    }

    @Override
    public Optional<TaxaCondominial> buscarPorId(TaxaId id) {
        String sql = "SELECT * FROM taxa_condominial WHERE id = ?";
        List<TaxaCondominial> resultados = jdbcTemplate.query(sql, rowMapper, id.getValor());
        return resultados.stream().findFirst();
    }

    @Override
    public List<TaxaCondominial> listarPorUnidade(UnidadeId unidadeId) {
        String sql = "SELECT * FROM taxa_condominial WHERE unidade_id = ?";
        return jdbcTemplate.query(sql, rowMapper, unidadeId.getValor());
    }

    @Override
    public List<TaxaCondominial> listarTodas() {
        String sql = "SELECT * FROM taxa_condominial";
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    public boolean existeTaxaAtrasadaPorUnidade(UnidadeId unidadeId) {
        String sql = "SELECT COUNT(*) FROM taxa_condominial WHERE unidade_id = ? AND status = 'ATRASADA'";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, unidadeId.getValor());
        return count != null && count > 0;
    }
}