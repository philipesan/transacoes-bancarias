package com.victorarakaki.transacoes.conta.api;

import com.victorarakaki.transacoes.conta.api.request.CriarContaRequestDTO;
import com.victorarakaki.transacoes.conta.api.response.CriarContaResponseDTO;
import com.victorarakaki.transacoes.conta.domain.Conta;
import com.victorarakaki.transacoes.conta.domain.cqrs.command.CriarContaCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContaWebMapper {

    CriarContaCommand paraCriarCommando(CriarContaRequestDTO criarContaRequestDTO);

    @Mapping(source = "saldo", target = "saldoInicial")
    CriarContaResponseDTO paraCriarResponse(Conta conta);
}
