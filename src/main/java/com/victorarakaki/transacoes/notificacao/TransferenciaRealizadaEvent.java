package com.victorarakaki.transacoes.notificacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferenciaRealizadaEvent(
    UUID contaOrigemId, UUID contaDestinoId, BigDecimal valor, LocalDateTime realizadaEm) {}
