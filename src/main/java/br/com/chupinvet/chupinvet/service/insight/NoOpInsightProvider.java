package br.com.chupinvet.chupinvet.service.insight;

import br.com.chupinvet.chupinvet.model.Diario;
import org.springframework.stereotype.Service;

/**
 * Implementação provisória: não gera nenhum insight.
 */
@Service
public class NoOpInsightProvider implements InsightProvider {

    @Override
    public String gerarInsight(Diario diarioAtual, Diario diarioAnterior) {
        return null;
    }
}