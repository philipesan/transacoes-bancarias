package com.victorarakaki.transacoes.movimentacao.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovimentacaoRepository extends JpaRepository<MovimentacaoEntity, UUID> {
    List<MovimentacaoEntity> findByConta_IdOrderByRealizadaEmDesc(UUID contaId);

}
