package com.victorarakaki.transacoes.transferencia.api;

import com.victorarakaki.transacoes.transferencia.api.request.TransferenciaRequestDTO;
import com.victorarakaki.transacoes.transferencia.api.response.TransferenciaResponseDTO;
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

@RequestMapping("/transferencias")
@Tag(name = "Transferências", description = "APIs referentes a realização de transferências")
public interface TransferenciaController {

    @Operation(
            summary = "Realizar transferência",
            description = "Realiza uma transferência de valor entre duas contas")
    @ApiResponse(
            responseCode = "200",
            description = "Transferência realizada com sucesso",
            content =
            @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TransferenciaResponseDTO.class)))
    @ApiResponse(
            responseCode = "400",
            description = "Dados de entrada inválidos",
            content = @Content(mediaType = "application/problem+json"))
    @ApiResponse(
            responseCode = "404",
            description = "Conta de origem ou conta de destino não encontrada",
            content = @Content(mediaType = "application/problem+json"))
  @PostMapping
  ResponseEntity<TransferenciaResponseDTO> transferencias(
      @Valid @RequestBody TransferenciaRequestDTO request);
}
