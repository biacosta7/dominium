package com.dominium.backend.infrastructure.persistence.financeiro;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.dominium.backend.domain.financeiro.Orcamento;
import com.dominium.backend.domain.financeiro.repository.OrcamentoRepository;

@Repository
public class OrcamentoRepositoryImpl implements OrcamentoRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrcamentoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Orcamento> rowMapper = new RowMapper<Orcamento>() {
        @Override
        public Orcamento mapRow(ResultSet rs, int rowNum) throws SQLException {
            Orcamento o = new Orcamento();
            o.setId(rs.getLong("id"));
            o.setAno(rs.getInt("ano"));
            o.setValorTotal(rs.getBigDecimal("valor_total"));
            o.setValorGasto(rs.getBigDecimal("valor_gasto"));
            if (rs.getTimestamp("created_at") != null) o.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            if (rs.getTimestamp("updated_at") != null) o.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return o;
        }
    };

    @Override
    public Orcamento save(Orcamento orcamento) {
        if (orcamento.getId() == null) {
            String sql = "INSERT INTO orcamentos(ano, valor_total, valor_gasto) VALUES (?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, orcamento.getAno());
                ps.setBigDecimal(2, orcamento.getValorTotal());
                ps.setBigDecimal(3, orcamento.getValorGasto());
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                orcamento.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE orcamentos SET ano = ?, valor_total = ?, valor_gasto = ? WHERE id = ?";
            jdbcTemplate.update(sql, 
                orcamento.getAno(), 
                orcamento.getValorTotal(), 
                orcamento.getValorGasto(), 
                orcamento.getId());
        }
        return orcamento;
    }

    @Override
    public Optional<Orcamento> findById(Long id) {
        List<Orcamento> results = jdbcTemplate.query("SELECT * FROM orcamentos WHERE id = ?", rowMapper, id);
        return results.stream().findFirst();
    }

    @Override
    public Optional<Orcamento> findByAno(Integer ano) {
        List<Orcamento> results = jdbcTemplate.query("SELECT * FROM orcamentos WHERE ano = ?", rowMapper, ano);
        return results.stream().findFirst();
    }

    @Override
    public List<Orcamento> findAll() {
        return jdbcTemplate.query("SELECT * FROM orcamentos", rowMapper);
    }
}
