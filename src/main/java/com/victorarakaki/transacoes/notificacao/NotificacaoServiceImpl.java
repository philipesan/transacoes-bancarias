package com.victorarakaki.transacoes.notificacao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;

@Slf4j
@RequiredArgsConstructor
public class NotificacaoServiceImpl implements NotificacaoService {

    private static final String TRANSFERENCIA_REALIZADA = "Notificação: Transferencia recebida de %s no valor de %s em %s";

    @Override
    public void notificaTransferenciaCompleta(TransferenciaRealizadaEvent evento) {
        String dataFormatada = evento.realizadaEm().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        log.info(TRANSFERENCIA_REALIZADA.formatted(evento.contaOrigemId(), evento.valor(), dataFormatada));
    }
}
