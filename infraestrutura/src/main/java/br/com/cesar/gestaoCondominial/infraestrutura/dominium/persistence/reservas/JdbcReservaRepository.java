package br.com.cesar.gestaoCondominial.infraestrutura.dominium.persistence.reservas;

import br.com.cesar.gestaoCondominial.dominio.dominium.areacomum.AreaComumId;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.Reserva;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.ReservaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.StatusReserva;
import br.com.cesar.gestaoCondominial.dominio.dominium.reservas.repository.ReservaRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.repository.UnidadeRepository;
import br.com.cesar.gestaoCondominial.dominio.dominium.unidade.UnidadeId;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.UsuarioId;
import br.com.cesar.gestaoCondominial.dominio.dominium.usuario.repository.UsuarioRepository;
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
public class JdbcReservaRepository implements ReservaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UnidadeRepository unidadeRepository;
    private final UsuarioRepository usuarioRepository;

    public JdbcReservaRepository(JdbcTemplate jdbcTemplate, UnidadeRepository unidadeRepository, UsuarioRepository usuarioRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.unidadeRepository = unidadeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private final RowMapper<Reserva> rowMapper = new RowMapper<Reserva>() {
        @Override
        public Reserva mapRow(ResultSet rs, int rowNum) throws SQLException {
            ReservaId id = ReservaId.de(rs.getString("id"));
            AreaComumId areaId = new AreaComumId(rs.getLong("area_comum_id"));
            
            return Reserva.reconstituir(
                    id,
                    areaId,
                    new UnidadeId(rs.getLong("unidade_id")),
                    new UsuarioId(rs.getLong("usuario_id")),
                    rs.getDate("data_reserva").toLocalDate(),
                    rs.getTime("hora_inicio").toLocalTime(),
                    rs.getTime("hora_fim").toLocalTime(),
                    StatusReserva.valueOf(rs.getString("status"))
            );
        }
    };

    @Override
    public Reserva save(Reserva reserva) {
        Optional<Reserva> existente = findById(reserva.getId());

        if (existente.isEmpty()) {
            String sql = "INSERT INTO reservas (id, area_comum_id, unidade_id, usuario_id, data_reserva, hora_inicio, hora_fim, status, data_expira_confirmacao) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    reserva.getId().getValor(),
                    reserva.getAreaComumId().getValor(),
                    reserva.getUnidadeId().getId(),
                    reserva.getUsuarioId().getId(),
                    reserva.getDataReserva(),
                    reserva.getHoraInicio(),
                    reserva.getHoraFim(),
                    reserva.getStatus().name(),
                    reserva.getDataExpiraConfirmacao()
            );
        } else {
            String sql = "UPDATE reservas SET area_comum_id = ?, unidade_id = ?, usuario_id = ?, data_reserva = ?, hora_inicio = ?, hora_fim = ?, status = ?, data_expira_confirmacao = ? WHERE id = ?";
            jdbcTemplate.update(sql,
                    reserva.getAreaComumId().getValor(),
                    reserva.getUnidadeId().getId(),
                    reserva.getUsuarioId().getId(),
                    reserva.getDataReserva(),
                    reserva.getHoraInicio(),
                    reserva.getHoraFim(),
                    reserva.getStatus().name(),
                    reserva.getDataExpiraConfirmacao(),
                    reserva.getId().getValor()
            );
        }
        return reserva;
    }

    @Override
    public Optional<Reserva> findById(ReservaId id) {
        List<Reserva> results = jdbcTemplate.query("SELECT * FROM reservas WHERE id = ?", rowMapper, id.getValor());
        return results.stream().findFirst();
    }

    @Override
    public List<Reserva> buscarPorUsuario(UsuarioId usuarioId) {
        return jdbcTemplate.query("SELECT * FROM reservas WHERE usuario_id = ?", rowMapper, usuarioId.getId());
    }

    @Override
    public List<Reserva> buscarAtivasPorPeriodo(AreaComumId areaComumId, LocalDate data, LocalTime inicio, LocalTime fim) {
        String sql = "SELECT * FROM reservas WHERE area_comum_id = ? AND data_reserva = ? AND status NOT IN ('CANCELADA', 'CONCLUIDA') " +
                "AND ((hora_inicio < ? AND hora_fim > ?) OR (hora_inicio < ? AND hora_fim > ?) OR (hora_inicio >= ? AND hora_fim <= ?))";
        
        return jdbcTemplate.query(sql, rowMapper, 
                areaComumId.getValor(), 
                data, 
                fim, inicio, 
                fim, inicio,
                inicio, fim);
    }
}
