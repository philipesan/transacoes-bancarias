package com.victorarakaki.transacoes.comum;

import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail tratarValidacaoRequest(MethodArgumentNotValidException ex) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

    log.warn("Erro [{}] ao processar requisição", ex.getClass().getSimpleName(), ex);

    List<String> erros =
        ex.getBindingResult().getFieldErrors().stream()
            .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
            .toList();

    problem.setTitle("Requisição inválida");
    problem.setDetail("A requisição possui campos inválidos.");
    problem.setType(URI.create("/problemas/requisicao-invalida"));
    problem.setProperty("codigo", "REQUISICAO_INVALIDA");
    problem.setProperty("erros", erros);

    return problem;
  }

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ProblemDetail tratarValidacaoParametro(HandlerMethodValidationException ex) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

    log.warn("Erro [{}] ao processar requisição", ex.getClass().getSimpleName(), ex);

    problem.setTitle("Parâmetro inválido");
    problem.setDetail("Um ou mais parâmetros da requisição são inválidos.");
    problem.setType(URI.create("/problemas/parametro-invalido"));
    problem.setProperty("codigo", "PARAMETRO_INVALIDO");

    return problem;
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail tratarViolacaoIntegridade(DataIntegrityViolationException ex) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

    log.warn("Erro [{}] ao processar requisição", ex.getClass().getSimpleName(), ex);

    problem.setTitle("Violação de integridade");
    problem.setDetail("Os dados informados violam uma regra de integridade.");
    problem.setType(URI.create("/problemas/violacao-integridade"));
    problem.setProperty("codigo", "VIOLACAO_INTEGRIDADE");

    return problem;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail tratarErroInesperado(Exception ex) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);

    log.error("Erro inesperado [{}] ao processar requisição", ex.getClass().getSimpleName(), ex);

    problem.setTitle("Erro interno");
    problem.setDetail("Ocorreu um erro inesperado.");
    problem.setType(URI.create("/problemas/erro-interno"));
    problem.setProperty("codigo", "ERRO_INTERNO");

    return problem;
  }
}
