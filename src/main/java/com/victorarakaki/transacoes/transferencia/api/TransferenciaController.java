package com.victorarakaki.transacoes.transferencia.api;

import com.victorarakaki.transacoes.transferencia.api.request.TransferenciaRequestDTO;
import com.victorarakaki.transacoes.transferencia.api.response.TransferenciaResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/transferencias")
@Tag(name = "Transferências", description = "APIs referentes a realização de transferências")
public interface TransferenciaController {
  @PostMapping
  ResponseEntity<TransferenciaResponseDTO> transferencias(
      @Valid @RequestBody TransferenciaRequestDTO request);
}
