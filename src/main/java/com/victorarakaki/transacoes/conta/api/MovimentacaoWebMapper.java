package com.victorarakaki.transacoes.conta.api;

import com.victorarakaki.transacoes.conta.api.response.MovimentacaoResponseDTO;
import com.victorarakaki.transacoes.transferencia.movimentacao.application.Movimentacao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovimentacaoWebMapper {

  MovimentacaoResponseDTO paraMovimentacaoResponseDTO(Movimentacao movimentacao);
}
