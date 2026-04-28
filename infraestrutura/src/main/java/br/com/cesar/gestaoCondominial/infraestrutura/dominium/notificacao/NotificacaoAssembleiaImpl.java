package br.com.cesar.gestaoCondominial.infraestrutura.dominium.notificacao;

import br.com.cesar.gestaoCondominial.dominio.dominium.assembleia.Assembleia;
import br.com.cesar.gestaoCondominial.dominio.dominium.assembleia.service.ServicoNotificacaoAssembleia;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificacaoAssembleiaImpl implements ServicoNotificacaoAssembleia {

    private final JdbcTemplate jdbcTemplate;

    public NotificacaoAssembleiaImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void notificarMoradores(Assembleia assembleia) {
        List<Map<String, Object>> moradores = jdbcTemplate.queryForList(
            "SELECT id FROM usuarios WHERE tipo = 'MORADOR'"
        );

        for (Map<String, Object> morador : moradores) {
            Long moradorId = ((Number) morador.get("id")).longValue();
            jdbcTemplate.update(
                "INSERT INTO notificacoes_assembleia (assembleia_id, usuario_id, notificado_em) VALUES (?, ?, ?)",
                assembleia.getId().getValor(),
                moradorId,
                Timestamp.valueOf(LocalDateTime.now())
            );
        }
    }
}
