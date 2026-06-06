package com.victorarakaki.transacoes.transferencia.api.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferenciaResponseDTO(
    UUID id,
    UUID contaOrigemId,
    UUID contaDestinoId,
    BigDecimal valor,
    LocalDateTime realizadaEm) {}
