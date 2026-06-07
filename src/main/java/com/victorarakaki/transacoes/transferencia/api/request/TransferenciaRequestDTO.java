package com.victorarakaki.transacoes.transferencia.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
@Schema(description = "Dados necessários para realizar uma transferência entre contas")
public record TransferenciaRequestDTO(
        @Schema(
                description = "Identificador da conta de origem da transferência",
                example = "550e8400-e29b-41d4-a716-446655440000",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        UUID contaOrigemId,

        @Schema(
                description = "Identificador da conta de destino da transferência",
                example = "7f3d9a6e-9c41-4e2f-8e38-1b5a9c6f4a12",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        UUID contaDestinoId,

        @Schema(
                description = "Valor da transferência. Deve ser de pelo menos 1 centavo",
                example = "150.75",
                minimum = "0.01",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @DecimalMin(value = "0.01", message = "Valor deve ser pelo menos 1 centavo")
        BigDecimal valor) {}
