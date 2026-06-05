package com.victorarakaki.transacoes.transferencia.application.cqrs.command;

import java.math.BigDecimal;
import java.util.UUID;

public record RealizarTransferenciaCommand(
    UUID contaOrigemId, UUID contaDestinoId, BigDecimal valor) {}
