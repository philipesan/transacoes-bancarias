package com.victorarakaki.transacoes.conta.application.cqrs.command;

import java.math.BigDecimal;

public record CriarContaCommand(String nome, BigDecimal saldoInicial) {}
