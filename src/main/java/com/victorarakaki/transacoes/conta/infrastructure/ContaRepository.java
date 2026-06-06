package com.victorarakaki.transacoes.conta.infrastructure;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContaRepository extends JpaRepository<ContaEntity, UUID> {

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select c from ContaEntity c where c.id = :id")
  Optional<ContaEntity> findByIdForUpdate(@Param("id") UUID id);
}
