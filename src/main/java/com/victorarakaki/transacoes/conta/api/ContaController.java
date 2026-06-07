package com.victorarakaki.transacoes.conta.api;

import com.victorarakaki.transacoes.conta.api.request.CriarContaRequestDTO;
import com.victorarakaki.transacoes.conta.api.response.CriarContaResponseDTO;
import com.victorarakaki.transacoes.conta.api.response.MovimentacaoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(
            summary = "Consultar movimentações da conta",
            description = "Consulta as movimentações de uma conta pelo seu identificador")
    @ApiResponse(
            responseCode = "200",
            description = "Movimentações consultadas com sucesso",
            content =
            @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MovimentacaoResponseDTO.class))))
    @ApiResponse(
            responseCode = "400",
            description = "Identificador da conta inválido",
            content = @Content(mediaType = "application/problem+json"))
    @ApiResponse(
            responseCode = "404",
            description = "Conta não encontrada",
            content = @Content(mediaType = "application/problem+json"))
    @GetMapping("/{contaId}/movimentacoes")
    public List<MovimentacaoResponseDTO> consultarMovimentacoes(@PathVariable UUID contaId);
}
