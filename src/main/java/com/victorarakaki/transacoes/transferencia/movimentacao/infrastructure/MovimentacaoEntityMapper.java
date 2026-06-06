package com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure;

import com.victorarakaki.transacoes.transferencia.movimentacao.application.Movimentacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovimentacaoEntityMapper {
    @Mapping(source = "conta.id", target = "contaId")
    @Mapping(source = "transferencia.id", target = "transferenciaId")
    Movimentacao deEntity(MovimentacaoEntity entity);
}
