package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notificacao.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.NotificacaoService;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EnviarNotificacaoUseCase {

    private final NotificacaoService notificacaoService;
    private final JdbcTemplate jdbcTemplate;

    public EnviarNotificacaoUseCase(NotificacaoService notificacaoService, JdbcTemplate jdbcTemplate) {
        this.notificacaoService = notificacaoService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void executar(Long sindicoId, String mensagem, String tipo, String publico) {
        TipoNotificacao tipoNotificacao = TipoNotificacao.valueOf(tipo);

        if ("SINDICO".equals(publico)) {
            notificacaoService.enviar(sindicoId, mensagem, tipoNotificacao);
        } else {
            List<Map<String, Object>> moradores = jdbcTemplate.queryForList(
                    "SELECT id FROM usuarios WHERE tipo = 'MORADOR'"
            );
            for (Map<String, Object> m : moradores) {
                Long moradorId = ((Number) m.get("id")).longValue();
                notificacaoService.enviar(moradorId, mensagem, tipoNotificacao);
            }
        }
    }
}
