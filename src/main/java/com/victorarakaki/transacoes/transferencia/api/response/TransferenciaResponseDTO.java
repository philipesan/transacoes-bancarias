package com.victorarakaki.transacoes.transferencia.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados da transferência realizada")
public record TransferenciaResponseDTO(
        @Schema(
                description = "Identificador da transferência realizada",
                example = "b8c2f92f-3ef2-4a25-9e4f-69f935b73c91")
        UUID id,

        @Schema(
                description = "Identificador da conta de origem da transferência",
                example = "550e8400-e29b-41d4-a716-446655440000")
        UUID contaOrigemId,

        @Schema(
                description = "Identificador da conta de destino da transferência",
                example = "7f3d9a6e-9c41-4e2f-8e38-1b5a9c6f4a12")
        UUID contaDestinoId,

        @Schema(
                description = "Valor transferido",
                example = "150.75")
        BigDecimal valor,

        @Schema(
                description = "Data e hora em que a transferência foi realizada",
                example = "2026-06-06T14:30:00")
        LocalDateTime realizadaEm) {}
