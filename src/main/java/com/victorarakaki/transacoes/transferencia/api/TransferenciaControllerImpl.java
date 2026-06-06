package com.victorarakaki.transacoes.transferencia.api;

import com.victorarakaki.transacoes.transferencia.api.request.TransferenciaRequestDTO;
import com.victorarakaki.transacoes.transferencia.api.response.TransferenciaResponseDTO;
import com.victorarakaki.transacoes.transferencia.application.cqrs.command.RealizarTransferenciaCommand;
import com.victorarakaki.transacoes.transferencia.application.service.TransferenciaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class TransferenciaControllerImpl implements TransferenciaController {

  private final TransferenciaService transferenciaService;
  private final TransferenciaWebMapper transferenciaWebMapper;

  @Override
  public ResponseEntity<TransferenciaResponseDTO> transferencias(TransferenciaRequestDTO request) {
    RealizarTransferenciaCommand comando =
        transferenciaWebMapper.paraRealizarTransferenciaCommand(request);
    return ResponseEntity.ok(
        transferenciaWebMapper.paraTransferenciaResponse(transferenciaService.realizar(comando)));
  }
}
