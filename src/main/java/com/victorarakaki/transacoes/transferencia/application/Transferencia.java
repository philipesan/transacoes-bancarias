package com.victorarakaki.transacoes.transferencia.application;

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
public class Transferencia {
  private UUID id;
  private UUID contaOrigemId;
  private UUID contaDestinoId;
  private BigDecimal valor;
  private LocalDateTime realizadaEm;
}
