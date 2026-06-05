package com.victorarakaki.transacoes.conta.api;

import com.victorarakaki.transacoes.conta.api.request.CriarContaRequestDTO;
import com.victorarakaki.transacoes.conta.api.response.CriarContaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/contas")
@Tag(name = "Contas", description = "APIs referentes a criação e consulta de contas bancarias")
public interface ContaController {

  @Operation(summary = "Criar espécie documental", description = "Cria nova espécie documental")
  @ApiResponse(
      responseCode = "201",
      description = "Conta Criada com sucesso",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = CriarContaRequestDTO.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Dados de entrada inválidos",
      content = @Content(mediaType = "application/problem+json"))
  @PostMapping
  ResponseEntity<CriarContaResponseDTO> criarEspecie(
      @Valid @RequestBody CriarContaRequestDTO request);
}
