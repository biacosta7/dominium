package com.dominium.backend.infrastructure.persistence.reservas;

import com.dominium.backend.domain.areacomum.AreaComumId;
import com.dominium.backend.domain.reservas.FilaDeEspera;
import com.dominium.backend.domain.reservas.repository.FilaDeEsperaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcFilaDeEsperaRepository implements FilaDeEsperaRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcFilaDeEsperaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<FilaDeEspera> rowMapper = new RowMapper<FilaDeEspera>() {
        @Override
        public FilaDeEspera mapRow(ResultSet rs, int rowNum) throws SQLException {
            FilaDeEspera f = new FilaDeEspera();
            f.setId(rs.getString("id"));
            f.setAreaComumId(new AreaComumId(rs.getLong("area_comum_id")));
            f.setUsuarioId(rs.getLong("usuario_id"));
            f.setDataDesejada(rs.getDate("data_desejada").toLocalDate());
            f.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
            f.setHoraFim(rs.getTime("hora_fim").toLocalTime());
            f.setDataCadastro(rs.getTimestamp("data_cadastro").toLocalDateTime());
            f.setStatus(FilaDeEspera.StatusFila.valueOf(rs.getString("status")));
            return f;
        }
    };

    @Override
    public FilaDeEspera salvar(FilaDeEspera fila) {
        Optional<FilaDeEspera> existente = buscarPorId(fila.getId());
        if (existente.isEmpty()) {
            String sql = "INSERT INTO fila_espera (id, area_comum_id, usuario_id, data_desejada, hora_inicio, hora_fim, data_cadastro, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                fila.getId(),
                fila.getAreaComumId().getValor(),
                fila.getUsuarioId(),
                fila.getDataDesejada(),
                fila.getHoraInicio(),
                fila.getHoraFim(),
                fila.getDataCadastro(),
                fila.getStatus().name()
            );
        } else {
            String sql = "UPDATE fila_espera SET status = ? WHERE id = ?";
            jdbcTemplate.update(sql, fila.getStatus().name(), fila.getId());
        }
        return fila;
    }

    @Override
    public Optional<FilaDeEspera> buscarPorId(String id) {
        List<FilaDeEspera> results = jdbcTemplate.query("SELECT * FROM fila_espera WHERE id = ?", rowMapper, id);
        return results.stream().findFirst();
    }

    @Override
    public List<FilaDeEspera> listarPorArea(AreaComumId areaId) {
        return jdbcTemplate.query("SELECT * FROM fila_espera WHERE area_comum_id = ? AND status = 'AGUARDANDO' ORDER BY data_cadastro ASC", rowMapper, areaId.getValor());
    }

    @Override
    public Optional<FilaDeEspera> buscarProximoNaFila(AreaComumId areaId, LocalDate data, LocalTime inicio, LocalTime fim) {
        // Regra: Ordem cronológica de cadastro para o mesmo período ou que conflite
        String sql = "SELECT * FROM fila_espera WHERE area_comum_id = ? AND data_desejada = ? AND status = 'AGUARDANDO' " +
                "AND ((hora_inicio < ? AND hora_fim > ?) OR (hora_inicio < ? AND hora_fim > ?) OR (hora_inicio >= ? AND hora_fim <= ?)) " +
                "ORDER BY data_cadastro ASC LIMIT 1";
        
        List<FilaDeEspera> results = jdbcTemplate.query(sql, rowMapper, 
            areaId.getValor(), 
            data, 
            fim, inicio,
            fim, inicio,
            inicio, fim);
            
        return results.stream().findFirst();
    }
}
