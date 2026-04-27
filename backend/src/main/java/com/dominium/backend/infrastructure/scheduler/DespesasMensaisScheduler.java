package com.dominium.backend.infrastructure.scheduler;

import com.dominium.backend.application.funcionario.usecase.GerarDespesasMensaisUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DespesasMensaisScheduler {

    private final GerarDespesasMensaisUseCase gerarDespesasUseCase;

    public DespesasMensaisScheduler(GerarDespesasMensaisUseCase gerarDespesasUseCase) {
        this.gerarDespesasUseCase = gerarDespesasUseCase;
    }

    // executa no 1º dia de cada mês à meia-noite
    @Scheduled(cron = "0 0 0 1 * ?")
    public void gerarDespesasMensais() {
        gerarDespesasUseCase.executar();
    }
}
