package br.com.cesar.gestaoCondominial.comunicacao.aplicacao.documento.usecase;

import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.Documento;
import br.com.cesar.gestaoCondominial.comunicacao.dominio.documento.repository.DocumentoRepository;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.NotificacaoService;
import br.com.cesar.gestaoCondominial.comunicacao.aplicacao.notification.TipoNotificacao;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificarDocumentosVencendoUseCase {

    private static final int DIAS_AVISO = 30;

    private final DocumentoRepository documentoRepository;
    private final NotificacaoService notificacaoService;

    public NotificarDocumentosVencendoUseCase(DocumentoRepository documentoRepository,
                                               NotificacaoService notificacaoService) {
        this.documentoRepository = documentoRepository;
        this.notificacaoService = notificacaoService;
    }

    public void executar() {
        // Assume documentRepository has findVencendoAte or list all and filter
        // Based on previous reads, Documento has venceEm(int dias)
        List<Documento> todos = documentoRepository.findAtivos();

        for (Documento d : todos) {
            if (d.venceEm(DIAS_AVISO)) {
                notificacaoService.enviar(
                    d.getSindicoId(),
                    "O documento \"" + d.getNome() + "\" vence em " + d.getDataValidade() + ". Providencie a renovação.",
                    TipoNotificacao.VENCIMENTO_DOCUMENTO
                );
            }
        }
    }
}
