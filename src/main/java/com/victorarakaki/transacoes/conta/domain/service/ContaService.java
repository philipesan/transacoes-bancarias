package com.victorarakaki.transacoes.conta.domain.service;

import com.victorarakaki.transacoes.conta.domain.Conta;
import com.victorarakaki.transacoes.conta.domain.cqrs.command.CriarContaCommand;

public interface ContaService {
    Conta criar(CriarContaCommand comando);
}
