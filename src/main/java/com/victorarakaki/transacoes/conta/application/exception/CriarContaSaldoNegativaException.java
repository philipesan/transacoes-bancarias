package com.victorarakaki.transacoes.conta.application.exception;

public class CriarContaSaldoNegativaException extends RuntimeException {
  public CriarContaSaldoNegativaException(String message) {
    super(message);
  }
}
