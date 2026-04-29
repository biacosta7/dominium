package br.com.cesar.gestaoCondominial.comunicacao.infraestrutura.notificacao;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.Notificacao;
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

    private final RowMapper<Notificacao> rowMapper = (rs, rowNum) -> {
        Notificacao n = new Notificacao();
        n.setId(rs.getLong("id"));
        n.setUsuarioId(rs.getLong("usuario_id"));
        n.setMensagem(rs.getString("mensagem"));
        n.setTipo(TipoNotificacao.valueOf(rs.getString("tipo")));
        n.setLida(rs.getBoolean("lida"));
        n.setCriadaEm(rs.getTimestamp("criada_em").toLocalDateTime());
        return n;
    };

    @Override
    public Notificacao save(Notificacao n) {
        if (n.getId() == null) {
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
            if (keyHolder.getKey() != null) {
                n.setId(keyHolder.getKey().longValue());
            }
        } else {
            jdbcTemplate.update(
                "UPDATE notificacoes SET lida = ? WHERE id = ?",
                n.isLida(), n.getId()
            );
        }
        return n;
    }

    @Override
    public Optional<Notificacao> findById(Long id) {
        List<Notificacao> results = jdbcTemplate.query(
            "SELECT * FROM notificacoes WHERE id = ?", rowMapper, id
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
}
