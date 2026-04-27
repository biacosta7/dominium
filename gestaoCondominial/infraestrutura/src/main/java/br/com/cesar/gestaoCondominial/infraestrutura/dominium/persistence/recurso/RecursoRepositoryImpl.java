package br.com.cesar.gestaoCondominial.infraestrutura.dominium.persistence.recurso;

import com.dominium.backend.domain.multa.MultaId;
import br.com.cesar.gestaoCondominial.dominio.dominium.recurso.Recurso;
import br.com.cesar.gestaoCondominial.dominio.dominium.recurso.RecursoId;
import com.dominium.backend.domain.recurso.StatusRecurso;
import br.com.cesar.gestaoCondominial.dominio.dominium.recurso.repository.RecursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RecursoRepositoryImpl implements RecursoRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void salvar(Recurso recurso) {
        String sql = "INSERT INTO recurso_multa (id, multa_id, morador_id, motivo, data_solicitacao, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                recurso.getId().getValue(),
                recurso.getMultaId(),
                recurso.getMoradorId(),
                recurso.getMotivo(),
                recurso.getDataSolicitacao(),
                recurso.getStatus().name()
        );
    }

    @Override
    public void atualizar(Recurso recurso) {
        String sql = "UPDATE recurso_multa SET status = ?, justificativa_sindico = ?, data_decisao = ? WHERE id = ?";

        jdbcTemplate.update(sql,
                recurso.getStatus().name(),
                recurso.getJustificativaSindico(),
                recurso.getDataDecisao(),
                recurso.getId().getValue()
        );
    }

    @Override
    public Optional<Recurso> buscarPorId(RecursoId id) {
        String sql = "SELECT * FROM recurso_multa WHERE id = ?";
        return jdbcTemplate.query(sql, this::mapRowToRecurso, id.getValue()).stream().findFirst();
    }

        private Recurso mapRowToRecurso(ResultSet rs, int rowNum) throws SQLException {
        return new Recurso(
                new RecursoId(rs.getObject("id", UUID.class)),
                new MultaId(rs.getLong("multa_id")),
                rs.getObject("morador_id", UUID.class),
                rs.getString("motivo"),
                rs.getTimestamp("data_solicitacao").toLocalDateTime(),
                StatusRecurso.valueOf(rs.getString("status")),
                rs.getString("justificativa_sindico"),
                rs.getTimestamp("data_decisao") != null ? rs.getTimestamp("data_decisao").toLocalDateTime() : null
        );
    }
}