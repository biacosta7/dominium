package com.dominium.backend.application.documento.usecase;

import com.dominium.backend.domain.documento.Documento;
import com.dominium.backend.domain.documento.repository.DocumentoRepository;
import com.dominium.backend.domain.shared.notification.NotificacaoService;
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
        LocalDate limite = LocalDate.now().plusDays(DIAS_AVISO);
        List<Documento> vencendo = documentoRepository.findVencendoAte(limite);

        for (Documento d : vencendo) {
            notificacaoService.enviar(
                d.getSindicoId(),
                "O documento \"" + d.getNome() + "\" vence em " + d.getDataValidade() + ". Providencie a renovação.",
                com.dominium.backend.domain.shared.notification.TipoNotificacao.VENCIMENTO_DOCUMENTO
            );
        }
    }
}
