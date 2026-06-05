package com.victorarakaki.transacoes.conta.domain.exception;

public class CriarContaSaldoNegativaException extends RuntimeException {
  public CriarContaSaldoNegativaException(String message) {
    super(message);
  }
}
