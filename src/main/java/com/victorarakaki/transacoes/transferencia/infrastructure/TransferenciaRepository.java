package com.victorarakaki.transacoes.transferencia.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransferenciaRepository extends JpaRepository<TransferenciaEntity, UUID> {}
