package com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure;

import com.victorarakaki.transacoes.conta.infrastructure.ContaEntity;
import com.victorarakaki.transacoes.transferencia.infrastructure.TransferenciaEntity;
import com.victorarakaki.transacoes.transferencia.movimentacao.application.MovimentacaoTipoEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "movimentacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoEntity {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "conta_id", nullable = false)
  private ContaEntity conta;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "transferencia_id", nullable = false)
  private TransferenciaEntity transferencia;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo", nullable = false)
  private MovimentacaoTipoEnum tipo;

  @Column(name = "valor", nullable = false)
  private BigDecimal valor;

  @Column(name = "saldo_apos", nullable = false)
  private BigDecimal saldoApos;

  @Column(name = "realizada_em", nullable = false)
  private LocalDateTime realizadaEm;
}
