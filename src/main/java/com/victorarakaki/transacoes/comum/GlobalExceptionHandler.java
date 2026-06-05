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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final URI PROBLEMA_REQUISICAO_INVALIDA =
            URI.create("/problemas/requisicao-invalida");

    private static final URI PROBLEMA_PARAMETRO_INVALIDO =
            URI.create("/problemas/parametro-invalido");

    private static final URI PROBLEMA_VIOLACAO_INTEGRIDADE =
            URI.create("/problemas/violacao-integridade");

    private static final URI PROBLEMA_ERRO_INTERNO =
            URI.create("/problemas/erro-interno");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail tratarValidacaoRequest(MethodArgumentNotValidException ex) {
        if (log.isWarnEnabled()) {
            log.warn("Erro de validação no corpo da requisição [{}]: {}",
                    ex.getClass().getSimpleName(),
                    ex.getMessage());
        }

        List<String> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> "%s: %s".formatted(erro.getField(), erro.getDefaultMessage()))
                .toList();

        var problema = criarProblema(
                HttpStatus.BAD_REQUEST,
                "Requisição inválida",
                "A requisição possui campos inválidos.",
                PROBLEMA_REQUISICAO_INVALIDA,
                "REQUISICAO_INVALIDA"
        );

        problema.setProperty("erros", erros);

        return problema;
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail tratarValidacaoParametro(HandlerMethodValidationException ex) {
        if (log.isWarnEnabled()) {
            log.warn("Erro de validação em parâmetro da requisição [{}]: {}",
                    ex.getClass().getSimpleName(),
                    ex.getMessage());
        }

        List<String> erros = ex.getParameterValidationResults()
                .stream()
                .flatMap(resultado -> resultado.getResolvableErrors()
                        .stream()
                        .map(erro -> {
                            String nomeParametro = resultado.getMethodParameter().getParameterName();

                            if (nomeParametro == null || nomeParametro.isBlank()) {
                                nomeParametro = "parâmetro";
                            }

                            return "%s: %s".formatted(nomeParametro, erro.getDefaultMessage());
                        }))
                .toList();

        var problema = criarProblema(
                HttpStatus.BAD_REQUEST,
                "Parâmetro inválido",
                "Um ou mais parâmetros da requisição são inválidos.",
                PROBLEMA_PARAMETRO_INVALIDO,
                "PARAMETRO_INVALIDO"
        );

        problema.setProperty("erros", erros);

        return problema;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail tratarTipoArgumentoInvalido(MethodArgumentTypeMismatchException ex) {
        var tipoEsperado = ex.getRequiredType() != null
                ? ex.getRequiredType().getSimpleName()
                : "tipo desconhecido";

        if (log.isWarnEnabled()) {
            log.warn("Tipo inválido para parâmetro '{}' esperado [{}], recebido [{}]",
                    ex.getName(),
                    tipoEsperado,
                    ex.getValue());
        }

        var detalhe = "O parâmetro '%s' possui valor inválido para o tipo %s"
                .formatted(ex.getName(), tipoEsperado);

        var problema = criarProblema(
                HttpStatus.BAD_REQUEST,
                "Parâmetro inválido",
                detalhe,
                PROBLEMA_PARAMETRO_INVALIDO,
                "PARAMETRO_INVALIDO"
        );

        problema.setProperty("parameter", ex.getName());
        problema.setProperty("expectedType", tipoEsperado);
        problema.setProperty("rejectedValue", ex.getValue());

        return problema;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail tratarViolacaoIntegridade(DataIntegrityViolationException ex) {
        if (log.isWarnEnabled()) {
            log.warn("Violação de integridade ao processar requisição [{}]: {}",
                    ex.getClass().getSimpleName(),
                    ex.getMostSpecificCause().getMessage());
        }

        return criarProblema(
                HttpStatus.BAD_REQUEST,
                "Violação de integridade",
                "Os dados informados violam uma regra de integridade.",
                PROBLEMA_VIOLACAO_INTEGRIDADE,
                "VIOLACAO_INTEGRIDADE"
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail tratarErroInesperado(Exception ex) {
        log.error("Erro inesperado [{}] ao processar requisição",
                ex.getClass().getSimpleName(),
                ex);

        return criarProblema(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                "Ocorreu um erro inesperado.",
                PROBLEMA_ERRO_INTERNO,
                "ERRO_INTERNO"
        );
    }

    private ProblemDetail criarProblema(
            HttpStatus status,
            String titulo,
            String detalhe,
            URI tipo,
            String codigo
    ) {
        var problema = ProblemDetail.forStatusAndDetail(status, detalhe);
        problema.setTitle(titulo);
        problema.setType(tipo);
        problema.setProperty("codigo", codigo);
        return problema;
    }
}

