package br.com.cesar.gestaoCondominial.financeiro.infraestrutura.recurso;

import br.com.cesar.gestaoCondominial.financeiro.dominio.multa.MultaId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.Recurso;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.RecursoId;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.StatusRecurso;
import br.com.cesar.gestaoCondominial.financeiro.dominio.recurso.repository.RecursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.List;
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
                recurso.getId().getValue().toString(),
                recurso.getMultaId().getValor(),
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
                recurso.getId().getValue().toString()
        );
    }

    @Override
    public Optional<Recurso> buscarPorId(RecursoId id) {
        String sql = "SELECT * FROM recurso_multa WHERE id = ?";
        return jdbcTemplate.query(sql, this::mapRowToRecurso, id.getValue().toString()).stream().findFirst();
    }

    @Override
    public List<Recurso> listarTodos() {
        return jdbcTemplate.query("SELECT * FROM recurso_multa ORDER BY data_solicitacao DESC", this::mapRowToRecurso);
    }

        private Recurso mapRowToRecurso(ResultSet rs, int rowNum) throws SQLException {
        return new Recurso(
                new RecursoId(UUID.fromString(rs.getString("id"))),
                new MultaId(rs.getLong("multa_id")),
                rs.getLong("morador_id"),
                rs.getString("motivo"),
                rs.getTimestamp("data_solicitacao").toLocalDateTime(),
                StatusRecurso.valueOf(rs.getString("status")),
                rs.getString("justificativa_sindico"),
                rs.getTimestamp("data_decisao") != null ? rs.getTimestamp("data_decisao").toLocalDateTime() : null
        );
    }
}
