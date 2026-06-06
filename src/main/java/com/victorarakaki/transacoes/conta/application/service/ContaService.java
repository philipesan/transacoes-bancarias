package com.victorarakaki.transacoes.conta.application.service;

import com.victorarakaki.transacoes.conta.application.Conta;
import com.victorarakaki.transacoes.conta.application.cqrs.command.CriarContaCommand;

public interface ContaService {
  Conta criar(CriarContaCommand comando);
}
