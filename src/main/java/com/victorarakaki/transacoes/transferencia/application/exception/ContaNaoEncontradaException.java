package com.victorarakaki.transacoes.transferencia.application.exception;

import java.util.UUID;

public class ContaNaoEncontradaException extends RuntimeException {
  public ContaNaoEncontradaException(UUID contaId) {
    super("Conta não encontrada: " + contaId);
  }
}
