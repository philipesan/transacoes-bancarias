package com.victorarakaki.transacoes.conta.application.exception;

public class CriarContaSaldoInvalidoException extends RuntimeException {
  public CriarContaSaldoInvalidoException(String message) {
    super(message);
  }
}
