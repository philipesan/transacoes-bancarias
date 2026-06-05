package com.victorarakaki.transacoes.conta.api.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CriarContaResponseDTO(
        UUID id,
        String nome,
        BigDecimal saldoInicial
) {}
