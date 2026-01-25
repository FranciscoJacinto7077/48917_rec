package upt.gestaodespesas.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import upt.gestaodespesas.service.RecorrenciaService;

@Component
public class RecorrenciaScheduler {

    private final RecorrenciaService recorrenciaService;

    public RecorrenciaScheduler(RecorrenciaService recorrenciaService) {
        this.recorrenciaService = recorrenciaService;
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void gerarDespesasRecorrentes() {
        recorrenciaService.gerarDespesasRecorrentesHoje();
    }
}
