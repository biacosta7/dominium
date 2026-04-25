package com.dominium.backend.infrastructure.persistence.areacomum;

import com.dominium.backend.domain.areacomum.AreaComum;
import com.dominium.backend.domain.areacomum.AreaComumId;
import com.dominium.backend.domain.areacomum.AreaComumRepository;
import com.dominium.backend.domain.areacomum.StatusArea;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JdbcAreaComumRepository implements AreaComumRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcAreaComumRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<AreaComum> rowMapper = (rs, rowNum) -> {
        AreaComum area = new AreaComum();
        area.setId(new AreaComumId(rs.getLong("id")));
        area.setNome(rs.getString("nome"));
        area.setCapacidadeMaxima(rs.getInt("capacidade_maxima"));
        area.setStatus(StatusArea.valueOf(rs.getString("status")));
        return area;
    };

    @Override
    public Optional<AreaComum> findById(AreaComumId id) {
        String sql = "SELECT * FROM areas_comuns WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, id.getValor()).stream().findFirst();
    }
}
