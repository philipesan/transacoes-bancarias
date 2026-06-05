package com.victorarakaki.transacoes.notificacao;

public interface NotificacaoService {

    void notificaTransferenciaCompleta(TransferenciaRealizadaEvent evento);
}