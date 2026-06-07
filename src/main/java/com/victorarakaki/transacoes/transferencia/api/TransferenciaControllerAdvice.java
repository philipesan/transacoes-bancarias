package com.victorarakaki.transacoes.transferencia.api;

import com.victorarakaki.transacoes.transferencia.application.exception.SaldoInsuficienteException;
import com.victorarakaki.transacoes.transferencia.application.exception.TransferenciaInvalidaException;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = TransferenciaControllerImpl.class)
public class TransferenciaControllerAdvice {

    private static final URI PROBLEMA_CONTA = URI.create("/problemas/transferencias");

    @ExceptionHandler(SaldoInsuficienteException.class)
    public ProblemDetail tratarSaldoInsuficiente(SaldoInsuficienteException ex) {
        if (log.isWarnEnabled()) {
            log.warn("Saldo insuficiente para realizar operação: {}", ex.getMessage());
        }

        return criarProblema(
                HttpStatus.BAD_REQUEST,
                "Saldo insuficiente",
                ex.getMessage(),
                "SALDO_INSUFICIENTE"
        );
    }

    @ExceptionHandler(TransferenciaInvalidaException.class)
    public ProblemDetail tratarTransferenciaInvalida(TransferenciaInvalidaException ex) {
        if (log.isWarnEnabled()) {
            log.warn("Transferência inválida: {}", ex.getMessage());
        }

        return criarProblema(
                HttpStatus.BAD_REQUEST,
                "Transferência inválida",
                ex.getMessage(),
                "TRANSFERENCIA_INVALIDA"
        );
    }

    private ProblemDetail criarProblema(
            HttpStatus status,
            String titulo,
            String detalhe,
            String codigo
    ) {
        var problema = ProblemDetail.forStatusAndDetail(status, detalhe);
        problema.setTitle(titulo);
        problema.setType(PROBLEMA_CONTA);
        problema.setProperty("codigo", codigo);
        return problema;
    }
}
