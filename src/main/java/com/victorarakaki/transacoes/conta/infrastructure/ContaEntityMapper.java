package com.victorarakaki.transacoes.conta.infrastructure;

import com.victorarakaki.transacoes.conta.domain.Conta;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContaEntityMapper {
    Conta deEntity(ContaEntity entity);
}
