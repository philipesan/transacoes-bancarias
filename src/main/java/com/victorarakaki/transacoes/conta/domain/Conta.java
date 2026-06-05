package com.victorarakaki.transacoes.conta.domain;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Conta {
  private UUID id;
  private String nome;
  private BigDecimal saldo;
}
