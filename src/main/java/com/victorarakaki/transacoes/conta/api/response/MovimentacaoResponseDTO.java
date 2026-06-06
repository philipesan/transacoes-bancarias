package com.victorarakaki.transacoes.conta.api.response;

import com.victorarakaki.transacoes.transferencia.movimentacao.application.MovimentacaoTipoEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MovimentacaoResponseDTO(
        UUID id,
        UUID contaId,
        UUID transferenciaId,
        MovimentacaoTipoEnum tipo,
        BigDecimal valor,
        BigDecimal saldoApos,
        LocalDateTime realizadaEm
) {
}