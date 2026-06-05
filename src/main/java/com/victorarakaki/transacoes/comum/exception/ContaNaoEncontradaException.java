package com.victorarakaki.transacoes.comum.exception;

import java.util.UUID;

public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException(UUID contaId) {
        super("Conta não encontrada: " + contaId);
    }
}
