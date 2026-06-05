package com.victorarakaki.transacoes.transferencia.api.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record TransferenciaRequestDTO(
    @NotNull UUID contaOrigemId,
    @NotNull UUID contaDestinoId,
    @DecimalMin(value = "0.01", message = "Valor deve ser pelo menos 1 centavo")
        BigDecimal valor) {}
