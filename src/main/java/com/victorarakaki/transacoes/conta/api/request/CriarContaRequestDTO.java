package com.victorarakaki.transacoes.conta.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(name = "CriarContaRequestDTO", description = "Dados para criação de nova conta bancaria")
public record CriarContaRequestDTO(
    @NotNull
        @Schema(
            description = "Nome do Correntista",
            example = "JOÃO SILVA",
            requiredMode = Schema.RequiredMode.REQUIRED)
        String nome,
    @Min(0)
        @NotNull
        @Schema(
            description = "Saldo inicial da conta",
            example = "25.00",
            requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal saldoInicial) {}
