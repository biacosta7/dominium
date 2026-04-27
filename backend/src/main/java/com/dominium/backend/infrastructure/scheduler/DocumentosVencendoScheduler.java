package com.dominium.backend.infrastructure.scheduler;

import com.dominium.backend.application.documento.usecase.NotificarDocumentosVencendoUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DocumentosVencendoScheduler {

    private final NotificarDocumentosVencendoUseCase useCase;

    public DocumentosVencendoScheduler(NotificarDocumentosVencendoUseCase useCase) {
        this.useCase = useCase;
    }

    // toda segunda-feira às 8h
    @Scheduled(cron = "0 0 8 * * MON")
    public void verificarDocumentosVencendo() {
        useCase.executar();
    }
}
