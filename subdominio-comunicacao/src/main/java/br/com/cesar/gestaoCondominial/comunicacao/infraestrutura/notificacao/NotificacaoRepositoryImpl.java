package br.com.cesar.gestaoCondominial.comunicacao.infraestrutura.notificacao;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.Notificacao;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.NotificacaoId;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.repository.NotificacaoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class NotificacaoRepositoryImpl implements NotificacaoRepository {

    private final JdbcTemplate jdbcTemplate;

    public NotificacaoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Notificacao> rowMapper = (rs, rowNum) ->
        Notificacao.reconstituir(
            NotificacaoId.de(rs.getLong("id")),
            rs.getLong("usuario_id"),
            rs.getString("mensagem"),
            TipoNotificacao.valueOf(rs.getString("tipo")),
            rs.getBoolean("lida"),
            rs.getTimestamp("criada_em").toLocalDateTime()
        );

    @Override
    public Notificacao save(Notificacao n) {
        if (n.getId() == null) {
            return inserir(n);
        } else {
            atualizar(n);
            return n;
        }
    }

    private Notificacao inserir(Notificacao n) {
        String sql = "INSERT INTO notificacoes (usuario_id, mensagem, tipo, lida, criada_em) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, n.getUsuarioId());
            ps.setString(2, n.getMensagem());
            ps.setString(3, n.getTipo().name());
            ps.setBoolean(4, n.isLida());
            ps.setTimestamp(5, Timestamp.valueOf(n.getCriadaEm()));
            return ps;
        }, keyHolder);

        Long idGerado = keyHolder.getKey() != null ? keyHolder.getKey().longValue() : null;
        if (idGerado == null) {
            throw new IllegalStateException("Falha ao obter ID gerado para Notificacao");
        }
        return Notificacao.reconstituir(
            NotificacaoId.de(idGerado),
            n.getUsuarioId(),
            n.getMensagem(),
            n.getTipo(),
            n.isLida(),
            n.getCriadaEm()
        );
    }

    private void atualizar(Notificacao n) {
        jdbcTemplate.update(
            "UPDATE notificacoes SET lida = ? WHERE id = ?",
            n.isLida(), n.getId().getValor()
        );
    }

    @Override
    public Optional<Notificacao> findById(NotificacaoId id) {
        List<Notificacao> results = jdbcTemplate.query(
            "SELECT * FROM notificacoes WHERE id = ?", rowMapper, id.getValor()
        );
        return results.stream().findFirst();
    }

    @Override
    public List<Notificacao> findByUsuarioId(Long usuarioId) {
        return jdbcTemplate.query(
            "SELECT * FROM notificacoes WHERE usuario_id = ? ORDER BY criada_em DESC",
            rowMapper, usuarioId
        );
    }

    @Override
    public List<Notificacao> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM notificacoes ORDER BY criada_em DESC",
            rowMapper
        );
    }
}
