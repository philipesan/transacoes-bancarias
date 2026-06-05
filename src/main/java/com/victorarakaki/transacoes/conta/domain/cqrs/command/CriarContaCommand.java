package com.victorarakaki.transacoes.conta.domain.cqrs.command;

import java.math.BigDecimal;

public record CriarContaCommand(
        String nome,
        BigDecimal saldoInicial
) {
}
