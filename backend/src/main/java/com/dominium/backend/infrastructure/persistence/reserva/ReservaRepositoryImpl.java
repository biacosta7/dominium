package com.dominium.backend.infrastructure.persistence.reserva;

import com.dominium.backend.domain.areacomum.AreaComumId;
import com.dominium.backend.domain.reservas.Reserva;
import com.dominium.backend.domain.reservas.ReservaId;
import com.dominium.backend.domain.reservas.StatusReserva;
import com.dominium.backend.domain.reservas.repository.ReservaRepository;
import com.dominium.backend.domain.unidade.Unidade;
import com.dominium.backend.domain.unidade.repository.UnidadeRepository;
import com.dominium.backend.domain.usuario.Usuario;
import com.dominium.backend.domain.usuario.UsuarioId;
import com.dominium.backend.domain.usuario.repository.UsuarioRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ReservaRepositoryImpl implements ReservaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UsuarioRepository usuarioRepository ;
    private final UnidadeRepository unidadeRepository;

    public ReservaRepositoryImpl(
            JdbcTemplate jdbcTemplate,
            UsuarioRepository usuarioRepository,
            UnidadeRepository unidadeRepository
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.usuarioRepository = usuarioRepository;
        this.unidadeRepository = unidadeRepository;
    }

    private final RowMapper<Reserva> rowMapper = (rs, rowNum) -> {

        Usuario usuario = usuarioRepository
                .findById(rs.getLong("usuario_id"))
                .orElse(null);

        Unidade unidade = unidadeRepository
                .findById(rs.getLong("unidade_id"))
                .orElse(null);

        return Reserva.reconstituir(
                ReservaId.de(rs.getString("id")),
                new AreaComumId(rs.getLong("area_comum_id")),
                usuario,
                unidade,
                rs.getDate("data_reserva").toLocalDate(),
                rs.getTime("hora_inicio").toLocalTime(),
                rs.getTime("hora_fim").toLocalTime(),
                StatusReserva.valueOf(rs.getString("status"))
        );
    };


    @Override
    public Reserva save(Reserva reserva) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM reserva WHERE id = ?",
                Integer.class,
                reserva.getId().getValor()
        );

        if (count == null || count == 0) {
            return inserir(reserva);
        }
        return atualizar(reserva);
    }

    private Reserva inserir(Reserva reserva) {
        String sql = """
                INSERT INTO reserva (id, area_comum_id, unidade_id, usuario_id, data_reserva, hora_inicio, hora_fim, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(sql,
                reserva.getId().getValor(),
                reserva.getAreaComumId().getValor(),
                reserva.getUnidadeId().getId(), //getValor() quando fizer os Ids
                reserva.getUsuarioId().getId(),
                reserva.getDataReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFim(),
                reserva.getStatus().name()
        );

        return reserva;
    }

    private Reserva atualizar(Reserva reserva) {
        String sql = """
                UPDATE reserva
                SET area_comum_id = ?,
                    unidade_id    = ?,
                    usuario_id    = ?,
                    data_reserva  = ?,
                    hora_inicio   = ?,
                    hora_fim      = ?,
                    status        = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql,
                reserva.getAreaComumId().getValor(),
                reserva.getUnidadeId().getId(),//mesmoq que em cima
                reserva.getUsuarioId().getId(),
                reserva.getDataReserva(),
                reserva.getHoraInicio(),
                reserva.getHoraFim(),
                reserva.getStatus().name(),
                reserva.getId().getValor()
        );

        return reserva;
    }


    @Override
    public Optional<Reserva> findById(ReservaId id) {
        List<Reserva> results = jdbcTemplate.query(
                "SELECT * FROM reserva WHERE id = ?",
                rowMapper,
                id.getValor()
        );

        return results.stream().findFirst();
    }


    @Override
    public List<Reserva> buscarPorUsuario(UsuarioId usuarioId) {
        return jdbcTemplate.query(
                "SELECT * FROM reserva WHERE usuario_id = ?",
                rowMapper,
                usuarioId.getValor()
        );
    }


    @Override
    public List<Reserva> buscarAtivasPorPeriodo(
            AreaComumId areaComumId,
            LocalDate data,
            LocalTime inicio,
            LocalTime fim
    ) {
        String sql = """
                SELECT * FROM reserva
                WHERE area_comum_id = ?
                  AND data_reserva  = ?
                  AND status NOT IN ('CANCELADA', 'CONCLUIDA')
                  AND hora_inicio   < ?
                  AND hora_fim      > ?
                """;

        return jdbcTemplate.query(sql, rowMapper,
                areaComumId.getValor(),
                data,
                fim,
                inicio
        );
    }
}