package com.victorarakaki.transacoes.transferencia.movimentacao.application.cqrs.query;

import java.util.UUID;

public record ConsultarMovimentacoesQuery(
        UUID contaId
) {}
