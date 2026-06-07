package com.victorarakaki.transacoes.conta.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Dados da conta criada")
public record CriarContaResponseDTO(
        @Schema(
                description = "Identificador da conta criada",
                example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(
                description = "Nome do titular ou identificação da conta",
                example = "João da Silva")
        String nome,

        @Schema(
                description = "Saldo inicial informado na criação da conta",
                example = "1000.00")
        BigDecimal saldoInicial) {}