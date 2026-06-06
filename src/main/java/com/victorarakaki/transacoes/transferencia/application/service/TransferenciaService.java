package com.victorarakaki.transacoes.transferencia.application.service;

import com.victorarakaki.transacoes.transferencia.application.Transferencia;
import com.victorarakaki.transacoes.transferencia.application.cqrs.command.RealizarTransferenciaCommand;

public interface TransferenciaService {
  Transferencia realizar(RealizarTransferenciaCommand comando);
}
