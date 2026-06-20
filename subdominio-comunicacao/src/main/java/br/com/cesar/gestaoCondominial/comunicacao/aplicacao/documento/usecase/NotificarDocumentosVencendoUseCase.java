package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.NotificacaoService;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.Documento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.DocumentoRepository;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao;
import org.springframework.stereotype.Service;

/**
 * Implementação concreta do Template Method AlertaDocumentoTemplate.
 *
 * Critério de alerta: documentos ativos que vencem nos próximos 30 dias.
 * Destinatário: síndico responsável pelo documento (comportamento padrão do template).
 */
@Service
public class NotificarDocumentosVencendoUseCase extends AlertaDocumentoTemplate {

    private static final int DIAS_AVISO = 30;

    public NotificarDocumentosVencendoUseCase(DocumentoRepository documentoRepository,
                                               NotificacaoService notificacaoService) {
        super(documentoRepository, notificacaoService);
    }

    @Override
    protected boolean deveAlertar(Documento documento) {
        return documento.venceEm(DIAS_AVISO);
    }

    @Override
    protected String construirMensagem(Documento documento) {
        return "O documento \"" + documento.getNome() + "\" vence em "
                + documento.getDataValidade() + ". Providencie a renovação.";
    }

    @Override
    protected TipoNotificacao obterTipoNotificacao() {
        return TipoNotificacao.VENCIMENTO_DOCUMENTO;
    }
}
