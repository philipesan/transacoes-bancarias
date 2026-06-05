package com.victorarakaki.transacoes.conta.api;

import com.victorarakaki.transacoes.conta.api.request.CriarContaRequestDTO;
import com.victorarakaki.transacoes.conta.api.response.CriarContaResponseDTO;
import com.victorarakaki.transacoes.conta.application.cqrs.command.CriarContaCommand;
import com.victorarakaki.transacoes.conta.application.service.ContaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class ContaControllerImpl implements ContaController {

  private final ContaService contaService;
  private final ContaWebMapper contaWebMapper;

  @Override
  public ResponseEntity<CriarContaResponseDTO> criarEspecie(CriarContaRequestDTO request) {
    log.info("Criação de Conta: Solicitação recebida de {}", request.nome());
    CriarContaCommand comando = contaWebMapper.paraCriarCommando(request);
    return ResponseEntity.ok(contaWebMapper.paraCriarResponse(contaService.criar(comando)));
  }
}
