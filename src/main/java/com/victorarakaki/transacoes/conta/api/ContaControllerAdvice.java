package com.victorarakaki.transacoes.conta.api;

import com.victorarakaki.transacoes.conta.domain.exception.CriarContaSaldoNegativaException;
import com.victorarakaki.transacoes.conta.domain.exception.NomeInvalidoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = ContaControllerImpl.class)
public class ContaControllerAdvice {
    @ExceptionHandler(CriarContaSaldoNegativaException.class)
    public ProblemDetail criarContaSaldoNegativo(CriarContaSaldoNegativaException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        log.warn("Erro: Contas não podem ter o saldo negativo");

        problem.setTitle("Conta com saldo Negativo");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("/problemas/conta-invalida"));
        problem.setProperty("codigo", "CONTA_SALDO_NEGATIVO");

        return problem;
    }

    @ExceptionHandler
    public ProblemDetail nomeInvalido(NomeInvalidoException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        log.warn("Erro: Contas devem ter um nome válido");

        problem.setTitle("Conta com nome inválido");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("/problemas/conta-invalida"));
        problem.setProperty("codigo", "CONTA_NOME_INVALIDO");

        return problem;
    }
}
