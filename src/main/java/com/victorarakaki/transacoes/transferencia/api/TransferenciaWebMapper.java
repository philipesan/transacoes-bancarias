package com.victorarakaki.transacoes.transferencia.api;

import com.victorarakaki.transacoes.transferencia.api.request.TransferenciaRequestDTO;
import com.victorarakaki.transacoes.transferencia.api.response.TransferenciaResponseDTO;
import com.victorarakaki.transacoes.transferencia.application.Transferencia;
import com.victorarakaki.transacoes.transferencia.application.cqrs.command.RealizarTransferenciaCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransferenciaWebMapper {
  RealizarTransferenciaCommand paraRealizarTransferenciaCommand(TransferenciaRequestDTO request);

  TransferenciaResponseDTO paraTransferenciaResponse(Transferencia transferencia);
}
