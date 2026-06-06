package com.victorarakaki.transacoes.transferencia.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferenciaRepository extends JpaRepository<TransferenciaEntity, UUID> {}
