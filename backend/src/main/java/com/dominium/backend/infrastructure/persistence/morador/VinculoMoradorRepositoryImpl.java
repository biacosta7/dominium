package com.dominium.backend.infrastructure.persistence.morador;

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

import com.dominium.backend.domain.morador.StatusVinculo;
import com.dominium.backend.domain.morador.TipoVinculo;
import com.dominium.backend.domain.morador.VinculoMorador;
import com.dominium.backend.domain.morador.repository.VinculoMoradorRepository;
import com.dominium.backend.domain.unidade.UnidadeId;
import com.dominium.backend.domain.unidade.repository.UnidadeRepository;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;

@Repository
public class VinculoMoradorRepositoryImpl implements VinculoMoradorRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UnidadeRepository unidadeRepository;
    private final UsuarioRepository usuarioRepository;

    public VinculoMoradorRepositoryImpl(JdbcTemplate jdbcTemplate, UnidadeRepository unidadeRepository, UsuarioRepository usuarioRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.unidadeRepository = unidadeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private RowMapper<VinculoMorador> rowMapper = new RowMapper<VinculoMorador>() {
        @Override
        public VinculoMorador mapRow(ResultSet rs, int rowNum) throws SQLException {
            VinculoMorador v = new VinculoMorador();
            v.setId(rs.getLong("id"));
            
            Long unidId = rs.getLong("unidade_id");
            v.setUnidade(unidadeRepository.findById(new UnidadeId(unidId)).orElse(null));
            
            Long usuId = rs.getLong("usuario_id");
            v.setUsuario(usuarioRepository.findById(usuId).orElse(null));
            
            v.setTipo(TipoVinculo.valueOf(rs.getString("tipo")));
            v.setStatus(StatusVinculo.valueOf(rs.getString("status")));
            
            if (rs.getTimestamp("created_at") != null) v.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            if (rs.getTimestamp("updated_at") != null) v.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            
            return v;
        }
    };

    @Override
    public VinculoMorador save(VinculoMorador vinculo) {
        if (vinculo.getId() == null) {
            String sql = "INSERT INTO vinculos_morador(unidade_id, usuario_id, tipo, status) VALUES (?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        sql,
                        new String[] { "id" }
                );
                ps.setLong(1, vinculo.getUnidade().getId().getValor());
                ps.setLong(2, vinculo.getUsuario().getId());
                ps.setString(3, vinculo.getTipo().name());
                ps.setString(4, vinculo.getStatus().name());
                return ps;
            }, keyHolder);
            if (keyHolder.getKey() != null) {
                vinculo.setId(keyHolder.getKey().longValue());
            }
        } else {
            String sql = "UPDATE vinculos_morador SET unidade_id = ?, usuario_id = ?, tipo = ?, status = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                vinculo.getUnidade().getId(), 
                vinculo.getUsuario().getId(), 
                vinculo.getTipo().name(), 
                vinculo.getStatus().name(), 
                vinculo.getId());
        }
        return vinculo;
    }

    @Override
    public Optional<VinculoMorador> findById(Long id) {
        List<VinculoMorador> results = jdbcTemplate.query("SELECT * FROM vinculos_morador WHERE id = ?", rowMapper, id);
        return results.stream().findFirst();
    }

    @Override
    public List<VinculoMorador> findByUnidadeIdAndStatus(Long unidadeId, StatusVinculo status) {
        return jdbcTemplate.query("SELECT * FROM vinculos_morador WHERE unidade_id = ? AND status = ?", rowMapper, unidadeId, status.name());
    }

    @Override
    public List<VinculoMorador> findByUsuarioIdAndStatus(Long usuarioId, StatusVinculo status) {
        return jdbcTemplate.query("SELECT * FROM vinculos_morador WHERE usuario_id = ? AND status = ?", rowMapper, usuarioId, status.name());
    }

    @Override
    public List<VinculoMorador> findByUsuarioAndUnidade(Long usuarioId, Long unidadeId) {

        String sql = """
        SELECT * FROM vinculo_morador
        WHERE usuario_id = ? AND unidade_id = ?
    """;

        return jdbcTemplate.query(sql, rowMapper, usuarioId, unidadeId);
    }

    @Override
    public long countByUnidadeIdAndStatus(Long unidadeId, StatusVinculo status) {
        Long count = jdbcTemplate.queryForObject("SELECT count(*) FROM vinculos_morador WHERE unidade_id = ? AND status = ?", Long.class, unidadeId, status.name());
        return count != null ? count : 0L;
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM vinculos_morador WHERE id = ?", id);
    }
}
