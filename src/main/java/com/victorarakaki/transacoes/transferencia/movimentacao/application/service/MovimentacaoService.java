package com.victorarakaki.transacoes.transferencia.movimentacao.application.service;

import com.victorarakaki.transacoes.transferencia.movimentacao.application.Movimentacao;
import com.victorarakaki.transacoes.transferencia.movimentacao.application.cqrs.query.ConsultarMovimentacoesQuery;
import java.util.List;

public interface MovimentacaoService {
    List<Movimentacao> consultarMovimentacoes(ConsultarMovimentacoesQuery consulta);
}
