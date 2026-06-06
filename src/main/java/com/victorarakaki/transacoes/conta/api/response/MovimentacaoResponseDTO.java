package com.victorarakaki.transacoes.conta.api.response;

import com.victorarakaki.transacoes.transferencia.movimentacao.application.MovimentacaoTipoEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Schema(description = "Dados de uma movimentação da conta")
public record MovimentacaoResponseDTO(
        @Schema(
                description = "Identificador da movimentação",
                example = "0f5e9e3a-4c2f-4b44-94d5-2f3c5f9d7a10")
        UUID id,

        @Schema(
                description = "Identificador da conta relacionada à movimentação",
                example = "550e8400-e29b-41d4-a716-446655440000")
        UUID contaId,

        @Schema(
                description = "Identificador da transferência que originou a movimentação",
                example = "b8c2f92f-3ef2-4a25-9e4f-69f935b73c91")
        UUID transferenciaId,

        @Schema(
                description = "Tipo da movimentação realizada",
                example = "DEBITO")
        MovimentacaoTipoEnum tipo,

        @Schema(
                description = "Valor movimentado",
                example = "150.75")
        BigDecimal valor,

        @Schema(
                description = "Saldo da conta após a movimentação",
                example = "849.25")
        BigDecimal saldoApos,

        @Schema(
                description = "Data e hora em que a movimentação foi realizada",
                example = "2026-06-06T14:30:00")
        LocalDateTime realizadaEm) {}