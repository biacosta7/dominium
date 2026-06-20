package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.NotificacaoService;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.notificacao.TipoNotificacao;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.Documento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.DocumentoRepository;

import java.util.List;

/**
 * Template Method — define o esqueleto do algoritmo de alertas baseados em documentos.
 *
 * Algoritmo fixo:
 *   1. obterDocumentos     — hook: por padrão retorna todos os ativos
 *   2. deveAlertar         — abstract: critério de elegibilidade do documento
 *   3. obterDestinatario   — hook: por padrão notifica o síndico do documento
 *   4. construirMensagem   — abstract: texto personalizado por tipo de alerta
 *   5. obterTipoNotificacao — abstract: enum do tipo de notificação
 *
 * Subclasses concretas:
 *   - NotificarDocumentosVencendoUseCase → alerta sobre documentos próximos do vencimento
 *
 * Novas classes podem ser adicionadas sem modificar este template:
 *   - AlertarDocumentosExpirados, AlertarDocumentosSemVersao, etc.
 */
public abstract class AlertaDocumentoTemplate {

    protected final DocumentoRepository documentoRepository;
    protected final NotificacaoService notificacaoService;

    protected AlertaDocumentoTemplate(DocumentoRepository documentoRepository,
                                      NotificacaoService notificacaoService) {
        this.documentoRepository = documentoRepository;
        this.notificacaoService = notificacaoService;
    }

    /**
     * Template method — final para garantir que a ordem dos passos não seja alterada.
     */
    public final void executar() {
        List<Documento> documentos = obterDocumentos();
        for (Documento documento : documentos) {
            if (deveAlertar(documento)) {
                Long destinatario = obterDestinatario(documento);
                String mensagem = construirMensagem(documento);
                notificacaoService.enviar(destinatario, mensagem, obterTipoNotificacao());
            }
        }
    }

    /**
     * Hook — subclasse pode sobrescrever para filtrar a coleção de entrada.
     * Padrão: retorna todos os documentos ativos.
     */
    protected List<Documento> obterDocumentos() {
        return documentoRepository.findAtivos();
    }

    /**
     * Abstract — define o critério que torna um documento elegível para alerta.
     */
    protected abstract boolean deveAlertar(Documento documento);

    /**
     * Hook — subclasse pode sobrescrever para alterar o destinatário.
     * Padrão: notifica o síndico responsável pelo documento.
     */
    protected Long obterDestinatario(Documento documento) {
        return documento.getSindicoId();
    }

    /**
     * Abstract — constrói a mensagem de notificação específica para o alerta.
     */
    protected abstract String construirMensagem(Documento documento);

    /**
     * Abstract — retorna o tipo de notificação associado ao alerta.
     */
    protected abstract TipoNotificacao obterTipoNotificacao();
}
