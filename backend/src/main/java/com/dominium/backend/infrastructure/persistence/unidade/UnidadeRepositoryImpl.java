package com.dominium.backend.infrastructure.persistence.unidade;

import java.util.List;
import java.util.Optional;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.dominium.backend.domain.unidade.StatusAdimplencia;
import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.unidade.repository.UnidadeRepository;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;

@Repository
public class UnidadeRepositoryImpl implements UnidadeRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UsuarioRepository usuarioRepository;

    public UnidadeRepositoryImpl(JdbcTemplate jdbcTemplate, UsuarioRepository usuarioRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.usuarioRepository = usuarioRepository;
    }

    private RowMapper<Unidade> rowMapper = new RowMapper<Unidade>() {
        @Override
        public Unidade mapRow(ResultSet rs, int rowNum) throws SQLException {
            Unidade u = new Unidade();
            u.setId(rs.getLong("id"));
            u.setNumero(rs.getString("numero"));
            u.setBloco(rs.getString("bloco"));
            
            Long propId = rs.getLong("proprietario_id");
            if (!rs.wasNull()) {
                u.setProprietario(usuarioRepository.findById(propId).orElse(null));
            }
            
            Long inqId = rs.getLong("inquilino_id");
            if (!rs.wasNull()) {
                u.setInquilino(usuarioRepository.findById(inqId).orElse(null));
            }
            
            String statusStr = rs.getString("status");
            if (statusStr != null) {
                u.setStatus(StatusAdimplencia.valueOf(statusStr));
            }
            u.setSaldoDevedor(rs.getBigDecimal("saldo_devedor"));
            if (rs.getTimestamp("created_at") != null) u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            if (rs.getTimestamp("updated_at") != null) u.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            
            return u;
        }
    };

    @Override
    public Unidade save(Unidade unidade) {
        if (unidade.getId() == null) {
            String sql = "INSERT INTO unidades(numero, bloco, proprietario_id, inquilino_id, status, saldo_devedor) VALUES (?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, unidade.getNumero());
                ps.setString(2, unidade.getBloco());
                ps.setLong(3, unidade.getProprietario().getId());
                if (unidade.getInquilino() != null) {
                    ps.setLong(4, unidade.getInquilino().getId());
                } else {
                    ps.setNull(4, java.sql.Types.BIGINT);
                }
                ps.setString(5, unidade.getStatus() != null ? unidade.getStatus().name() : null);
                ps.setBigDecimal(6, unidade.getSaldoDevedor());
                return ps;
            }, keyHolder);
            if (keyHolder.getKeys() != null) {
                Number id = (Number) keyHolder.getKeys().get("id");
                unidade.setId(id.longValue());
            }
        } else {
            String sql = "UPDATE unidades SET numero = ?, bloco = ?, proprietario_id = ?, inquilino_id = ?, status = ?, saldo_devedor = ? WHERE id = ?";
            jdbcTemplate.update(sql, 
                unidade.getNumero(), 
                unidade.getBloco(), 
                unidade.getProprietario().getId(), 
                unidade.getInquilino() != null ? unidade.getInquilino().getId() : null, 
                unidade.getStatus() != null ? unidade.getStatus().name() : null, 
                unidade.getSaldoDevedor(), 
                unidade.getId());
        }
        return unidade;
    }

    @Override
    public Optional<Unidade> findById(Long id) {
        List<Unidade> results = jdbcTemplate.query("SELECT * FROM unidades WHERE id = ?", rowMapper, id);
        return results.stream().findFirst();
    }

    @Override
    public Optional<Unidade> findByNumeroAndBloco(String numero, String bloco) {
        List<Unidade> results = jdbcTemplate.query("SELECT * FROM unidades WHERE numero = ? AND bloco = ?", rowMapper, numero, bloco);
        return results.stream().findFirst();
    }

    @Override
    public List<Unidade> findAll() {
        return jdbcTemplate.query("SELECT * FROM unidades", rowMapper);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM unidades WHERE id = ?", id);
    }
}