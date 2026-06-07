package com.victorarakaki.transacoes.transferencia.movimentacao.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Movimentacao {

  private UUID id;
  private UUID contaId;
  private UUID transferenciaId;
  private MovimentacaoTipoEnum tipo;
  private BigDecimal valor;
  private BigDecimal saldoApos;
  private LocalDateTime realizadaEm;
}
