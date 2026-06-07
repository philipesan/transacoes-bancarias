package com.victorarakaki.transacoes.movimentacao.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.victorarakaki.transacoes.conta.infrastructure.ContaEntity;
import com.victorarakaki.transacoes.conta.infrastructure.ContaRepository;
import com.victorarakaki.transacoes.transferencia.application.exception.ContaNaoEncontradaException;
import com.victorarakaki.transacoes.transferencia.infrastructure.TransferenciaEntity;
import com.victorarakaki.transacoes.transferencia.movimentacao.application.Movimentacao;
import com.victorarakaki.transacoes.transferencia.movimentacao.application.MovimentacaoTipoEnum;
import com.victorarakaki.transacoes.transferencia.movimentacao.application.cqrs.query.ConsultarMovimentacoesQuery;
import com.victorarakaki.transacoes.transferencia.movimentacao.application.service.MovimentacaoServiceImpl;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoEntity;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoEntityMapper;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class MovimentacaoServiceUnitTest {

  @Mock private ContaRepository contaRepository;

  @Mock private MovimentacaoRepository movimentacaoRepository;

  @Spy
  private MovimentacaoEntityMapper movimentacaoEntityMapper =
      Mappers.getMapper(MovimentacaoEntityMapper.class);

  @InjectMocks private MovimentacaoServiceImpl movimentacaoService;

  private UUID contaId = UUID.fromString("00000000-0000-0000-0000-000000000001");

  @Test
  @DisplayName("Deve consultar movimentações de uma conta")
  void deveConsultarMovimentacoesDeUmaConta() {
    // given
    ContaEntity conta =
        ContaEntity.builder()
            .id(contaId)
            .nome("JOÃO SILVA")
            .saldo(new BigDecimal("900.00"))
            .build();

    ContaEntity conta2 =
        ContaEntity.builder()
            .id(UUID.randomUUID())
            .nome("MARIA")
            .saldo(new BigDecimal("600.00"))
            .build();

    TransferenciaEntity transferencia =
        TransferenciaEntity.builder()
            .id(UUID.randomUUID())
            .contaOrigem(conta)
            .contaDestino(conta2)
            .valor(new BigDecimal("100.00"))
            .realizadaEm(LocalDateTime.now())
            .build();

    MovimentacaoEntity movimentacao =
        MovimentacaoEntity.builder()
            .id(UUID.randomUUID())
            .conta(conta)
            .transferencia(transferencia)
            .tipo(MovimentacaoTipoEnum.DEBITO)
            .valor(new BigDecimal("100.00"))
            .saldoApos(new BigDecimal("900.00"))
            .realizadaEm(LocalDateTime.now())
            .build();

    when(contaRepository.existsById(contaId)).thenReturn(true);
    when(movimentacaoRepository.findByConta_IdOrderByRealizadaEmDesc(contaId))
        .thenReturn(List.of(movimentacao));

    // when
    List<Movimentacao> resultado =
        movimentacaoService.consultarMovimentacoes(new ConsultarMovimentacoesQuery(contaId));

    // then
    assertThat(resultado).hasSize(1);
    assertThat(resultado.getFirst().getContaId()).isEqualTo(contaId);
    assertThat(resultado.getFirst().getTransferenciaId()).isEqualTo(transferencia.getId());
    assertThat(resultado.getFirst().getTipo()).isEqualTo(MovimentacaoTipoEnum.DEBITO);
    assertThat(resultado.getFirst().getValor()).isEqualByComparingTo("100.00");
    assertThat(resultado.getFirst().getSaldoApos()).isEqualByComparingTo("900.00");

    verify(contaRepository).existsById(contaId);
    verify(movimentacaoRepository).findByConta_IdOrderByRealizadaEmDesc(contaId);
  }

  @Test
  @DisplayName("Deve retornar lista vazia quando conta existir mas não tiver movimentações")
  void deveRetornarListaVaziaQuandoContaExistirMasNaoTiverMovimentacoes() {
    // given
    when(contaRepository.existsById(contaId)).thenReturn(true);
    when(movimentacaoRepository.findByConta_IdOrderByRealizadaEmDesc(contaId))
        .thenReturn(List.of());

    // when
    List<Movimentacao> resultado =
        movimentacaoService.consultarMovimentacoes(new ConsultarMovimentacoesQuery(contaId));

    // then
    assertThat(resultado).isEmpty();

    verify(contaRepository).existsById(contaId);
    verify(movimentacaoRepository).findByConta_IdOrderByRealizadaEmDesc(contaId);
  }

  @Test
  @DisplayName("Deve lançar exceção quando conta não existir")
  void deveLancarExcecaoQuandoContaNaoExistir() {
    // given
    when(contaRepository.existsById(contaId)).thenReturn(false);

    var comando = new ConsultarMovimentacoesQuery(contaId);

    // when / then
    assertThrows(
        ContaNaoEncontradaException.class,
        () -> movimentacaoService.consultarMovimentacoes(comando));

    verify(contaRepository).existsById(contaId);
    verifyNoInteractions(movimentacaoRepository);
  }
}
