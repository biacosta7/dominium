package br.com.cesar.gestaoCondominial.financeiro.infraestrutura.financeiro;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.Despesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.CategoriaDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.TipoDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.StatusDespesa;
import br.com.cesar.gestaoCondominial.financeiro.dominio.financeiro.repository.DespesaRepository;

@Repository
public class DespesaRepositoryImpl implements DespesaRepository {

    private final JdbcTemplate jdbcTemplate;

    public DespesaRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Despesa> rowMapper = new RowMapper<Despesa>() {
        @Override
        public Despesa mapRow(ResultSet rs, int rowNum) throws SQLException {
            Despesa d = new Despesa();
            d.setId(rs.getLong("id"));
            d.setDescricao(rs.getString("descricao"));
            d.setValor(rs.getBigDecimal("valor"));
            if (rs.getDate("data") != null)
                d.setData(rs.getDate("data").toLocalDate());
            d.setCategoria(CategoriaDespesa.valueOf(rs.getString("categoria")));
            d.setTipo(TipoDespesa.valueOf(rs.getString("tipo")));
            d.setStatus(StatusDespesa.valueOf(rs.getString("status")));
            d.setOrcamentoId(rs.getLong("orcamento_id"));
            if (rs.wasNull())
                d.setOrcamentoId(null);
            if (rs.getTimestamp("created_at") != null)
                d.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            if (rs.getTimestamp("updated_at") != null)
                d.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return d;
        }
    };

    @Override
    public Despesa save(Despesa despesa) {
        if (despesa.getId() == null) {
            String sql = "INSERT INTO despesas(descricao, valor, data, categoria, tipo, status, orcamento_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        sql,
                        new String[] { "id" });
                ps.setString(1, despesa.getDescricao());
                ps.setBigDecimal(2, despesa.getValor());
                ps.setDate(3, java.sql.Date.valueOf(despesa.getData()));
                ps.setString(4, despesa.getCategoria().name());
                ps.setString(5, despesa.getTipo().name());
                ps.setString(6, despesa.getStatus().name());
                if (despesa.getOrcamentoId() != null) {
                    ps.setLong(7, despesa.getOrcamentoId());
                } else {
                    ps.setNull(7, java.sql.Types.BIGINT);
                }
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                despesa.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE despesas SET descricao = ?, valor = ?, data = ?, categoria = ?, tipo = ?, status = ?, orcamento_id = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    despesa.getDescricao(),
                    despesa.getValor(),
                    java.sql.Date.valueOf(despesa.getData()),
                    despesa.getCategoria().name(),
                    despesa.getTipo().name(),
                    despesa.getStatus().name(),
                    despesa.getOrcamentoId(),
                    despesa.getId());
        }
        return despesa;
    }

    @Override
    public Optional<Despesa> findById(Long id) {
        List<Despesa> results = jdbcTemplate.query("SELECT * FROM despesas WHERE id = ?", rowMapper, id);
        return results.stream().findFirst();
    }

    @Override
    public List<Despesa> findByOrcamentoId(Long orcamentoId) {
        return jdbcTemplate.query("SELECT * FROM despesas WHERE orcamento_id = ?", rowMapper, orcamentoId);
    }

    @Override
    public List<Despesa> findAll() {
        return jdbcTemplate.query("SELECT * FROM despesas", rowMapper);
    }
}
