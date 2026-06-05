package com.victorarakaki.transacoes.transferencia.infrastructure;

import com.victorarakaki.transacoes.transferencia.application.Transferencia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferenciaEntityMapper {
  @Mapping(source = "contaOrigem.id", target = "contaOrigemId")
  @Mapping(source = "contaDestino.id", target = "contaDestinoId")
  Transferencia deEntity(TransferenciaEntity entity);
}
