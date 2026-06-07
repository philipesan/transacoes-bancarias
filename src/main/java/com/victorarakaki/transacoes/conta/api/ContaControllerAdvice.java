package com.victorarakaki.transacoes.conta.api;

import com.victorarakaki.transacoes.conta.application.exception.CriarContaSaldoInvalidoException;
import com.victorarakaki.transacoes.conta.application.exception.NomeInvalidoException;
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
@RestControllerAdvice(assignableTypes = ContaControllerImpl.class)
public class ContaControllerAdvice {

    private static final URI PROBLEMA_CONTA_INVALIDA = URI.create("/problemas/conta-invalida");

    @ExceptionHandler(CriarContaSaldoInvalidoException.class)
    public ProblemDetail criarContaSaldoInvalido(CriarContaSaldoInvalidoException ex) {
        if (log.isWarnEnabled()) {
            log.warn("Conta não pode ser criada com saldo inválido ou negativo: {}", ex.getMessage());
        }

        return criarProblema(
                "Conta com saldo negativo ou inválido",
                ex.getMessage(),
                "CONTA_SALDO_NEGATIVO"
        );
    }

    @ExceptionHandler(NomeInvalidoException.class)
    public ProblemDetail nomeInvalido(NomeInvalidoException ex) {
        if (log.isWarnEnabled()) {
            log.warn("Conta deve ter um nome válido: {}", ex.getMessage());
        }

        return criarProblema(
                "Conta com nome inválido",
                ex.getMessage(),
                "CONTA_NOME_INVALIDO"
        );
    }

    private ProblemDetail criarProblema(
            String titulo,
            String detalhe,
            String codigo
    ) {
        var problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalhe);
        problema.setTitle(titulo);
        problema.setType(PROBLEMA_CONTA_INVALIDA);
        problema.setProperty("codigo", codigo);
        return problema;
    }
}
