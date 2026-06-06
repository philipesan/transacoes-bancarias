package com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoRepository extends JpaRepository<MovimentacaoEntity, UUID> {
  List<MovimentacaoEntity> findByConta_IdOrderByRealizadaEmDesc(UUID contaId);
}
