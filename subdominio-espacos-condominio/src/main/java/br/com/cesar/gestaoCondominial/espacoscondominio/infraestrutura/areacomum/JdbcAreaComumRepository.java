package br.com.cesar.gestaoCondominial.espacoscondominio.infraestrutura.areacomum;

import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComum;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.AreaComumRepository;
import br.com.cesar.gestaoCondominial.espacoscondominio.dominio.areacomum.StatusArea;
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

    @Override
    public AreaComum save(AreaComum area) {
        if (area.getId() == null) {
            String sql = "INSERT INTO areas_comuns (nome, capacidade_maxima, status) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, area.getNome(), area.getCapacidadeMaxima(), area.getStatus().name());
        } else {
            String sql = "UPDATE areas_comuns SET nome = ?, capacidade_maxima = ?, status = ? WHERE id = ?";
            jdbcTemplate.update(sql, area.getNome(), area.getCapacidadeMaxima(), area.getStatus().name(),
                    area.getId().getValor());
        }
        return area;
    }
}
