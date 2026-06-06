package com.victorarakaki.transacoes.transferencia.application.exception;

public class SaldoInsuficienteException extends RuntimeException {
  public SaldoInsuficienteException(String message) {
    super(message);
  }
}
