package br.com.chupinvet.chupinvet.service.insight;

import br.com.chupinvet.chupinvet.model.Diario;

/**
 * Ponto de extensão para a geração do campo ds_insight_ia.
 *
 * Nesta sprint, o insight NÃO é gerado por nós, será responsabilidade de
 * uma API de IA externa, a ser integrada na próxima sprint.
 */
public interface InsightProvider {

    String gerarInsight(Diario diarioAtual, Diario diarioAnterior);
}