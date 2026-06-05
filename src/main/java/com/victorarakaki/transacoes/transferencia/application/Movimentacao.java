package com.victorarakaki.transacoes.transferencia.application;

import com.victorarakaki.transacoes.conta.application.Conta;
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
  private Conta conta;
  private Transferencia transferencia;
  private MovimentacaoTipoEnum tipo;
  private BigDecimal valor;
  private BigDecimal saldoApos;
  private LocalDateTime realizadaEm;
}
