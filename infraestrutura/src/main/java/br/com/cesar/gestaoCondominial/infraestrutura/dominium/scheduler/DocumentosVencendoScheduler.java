package br.com.cesar.gestaoCondominial.infraestrutura.dominium.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DocumentosVencendoScheduler {

    // TODO: portar NotificarDocumentosVencendoUseCase para nova arquitetura
    @Scheduled(cron = "0 0 8 * * MON")
    public void verificarDocumentosVencendo() {
    }
}
