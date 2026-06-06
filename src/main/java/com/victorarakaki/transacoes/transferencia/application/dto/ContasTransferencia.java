package com.victorarakaki.transacoes.transferencia.application.dto;

import com.victorarakaki.transacoes.conta.infrastructure.ContaEntity;

public record ContasTransferencia(ContaEntity contaOrigem, ContaEntity contaDestino) {}
