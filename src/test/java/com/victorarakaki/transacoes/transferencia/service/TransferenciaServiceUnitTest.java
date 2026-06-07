package com.victorarakaki.transacoes.transferencia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.victorarakaki.transacoes.conta.infrastructure.ContaEntity;
import com.victorarakaki.transacoes.conta.infrastructure.ContaRepository;
import com.victorarakaki.transacoes.notificacao.NotificacaoService;
import com.victorarakaki.transacoes.notificacao.TransferenciaRealizadaEvent;
import com.victorarakaki.transacoes.transferencia.application.Transferencia;
import com.victorarakaki.transacoes.transferencia.application.cqrs.command.RealizarTransferenciaCommand;
import com.victorarakaki.transacoes.transferencia.application.exception.ContaNaoEncontradaException;
import com.victorarakaki.transacoes.transferencia.application.exception.SaldoInsuficienteException;
import com.victorarakaki.transacoes.transferencia.application.exception.TransferenciaInvalidaException;
import com.victorarakaki.transacoes.transferencia.application.service.TransferenciaServiceImpl;
import com.victorarakaki.transacoes.transferencia.infrastructure.TransferenciaEntity;
import com.victorarakaki.transacoes.transferencia.infrastructure.TransferenciaEntityMapper;
import com.victorarakaki.transacoes.transferencia.infrastructure.TransferenciaRepository;
import com.victorarakaki.transacoes.transferencia.movimentacao.application.MovimentacaoTipoEnum;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoEntity;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class TransferenciaServiceUnitTest {

  @Mock private ContaRepository contaRepository;

  @Mock private TransferenciaRepository transferenciaRepository;

  @Mock private MovimentacaoRepository movimentacaoRepository;

  @Mock private NotificacaoService notificacaoService;

  @Spy
  private TransferenciaEntityMapper transferenciaEntityMapper =
      Mappers.getMapper(TransferenciaEntityMapper.class);

  @InjectMocks private TransferenciaServiceImpl transferenciaService;

  private static final UUID CONTA_ORIGEM_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000001");
  private static final UUID CONTA_DESTINO_ID =
      UUID.fromString("00000000-0000-0000-0000-000000000002");

  @Test
  @DisplayName("Deve realizar transferência com sucesso")
  void deveRealizarTransferenciaComSucesso() {
    // given
    ContextoTransferencia contexto = prepararTransferenciaComSucesso();

    // when
    Transferencia resultado = transferenciaService.realizar(contexto.comando());

    // then
    assertThat(resultado).isNotNull();
    assertThat(resultado.getContaOrigemId()).isEqualTo(CONTA_ORIGEM_ID);
    assertThat(resultado.getContaDestinoId()).isEqualTo(CONTA_DESTINO_ID);
    assertThat(resultado.getValor()).isEqualByComparingTo("100.00");

    assertThat(contexto.contaOrigem().getSaldo()).isEqualByComparingTo("900.00");
    assertThat(contexto.contaDestino().getSaldo()).isEqualByComparingTo("600.00");

    verify(contaRepository).findByIdForUpdate(CONTA_ORIGEM_ID);
    verify(contaRepository).findByIdForUpdate(CONTA_DESTINO_ID);
    verify(contaRepository).save(contexto.contaOrigem());
    verify(contaRepository).save(contexto.contaDestino());
  }

  @Test
  @DisplayName("Deve persistir transferência e movimentações ao realizar transferência com sucesso")
  void devePersistirTransferenciaEMovimentacoesAoRealizarTransferenciaComSucesso() {
    // given
    ContextoTransferencia contexto = prepararTransferenciaComSucesso();

    // when
    transferenciaService.realizar(contexto.comando());

    // then
    ArgumentCaptor<TransferenciaEntity> transferenciaCaptor =
        ArgumentCaptor.forClass(TransferenciaEntity.class);

    verify(transferenciaRepository).saveAndFlush(transferenciaCaptor.capture());

    TransferenciaEntity transferenciaSalva = transferenciaCaptor.getValue();

    assertThat(transferenciaSalva.getContaOrigem()).isEqualTo(contexto.contaOrigem());
    assertThat(transferenciaSalva.getContaDestino()).isEqualTo(contexto.contaDestino());
    assertThat(transferenciaSalva.getValor()).isEqualByComparingTo("100.00");
    assertThat(transferenciaSalva.getRealizadaEm()).isNotNull();

    ArgumentCaptor<List<MovimentacaoEntity>> movimentacoesCaptor =
        ArgumentCaptor.forClass(List.class);

    verify(movimentacaoRepository).saveAllAndFlush(movimentacoesCaptor.capture());

    List<MovimentacaoEntity> movimentacoes = movimentacoesCaptor.getValue();

    assertThat(movimentacoes).hasSize(2);

    assertThat(movimentacoes)
        .extracting(MovimentacaoEntity::getTipo)
        .containsExactlyInAnyOrder(MovimentacaoTipoEnum.DEBITO, MovimentacaoTipoEnum.CREDITO);

    MovimentacaoEntity debito =
        obterMovimentacaoPorTipo(movimentacoes, MovimentacaoTipoEnum.DEBITO);

    MovimentacaoEntity credito =
        obterMovimentacaoPorTipo(movimentacoes, MovimentacaoTipoEnum.CREDITO);

    assertThat(debito.getConta()).isEqualTo(contexto.contaOrigem());
    assertThat(debito.getTransferencia()).isEqualTo(transferenciaSalva);
    assertThat(debito.getValor()).isEqualByComparingTo("100.00");
    assertThat(debito.getSaldoApos()).isEqualByComparingTo("900.00");

    assertThat(credito.getConta()).isEqualTo(contexto.contaDestino());
    assertThat(credito.getTransferencia()).isEqualTo(transferenciaSalva);
    assertThat(credito.getValor()).isEqualByComparingTo("100.00");
    assertThat(credito.getSaldoApos()).isEqualByComparingTo("600.00");
  }

  @Test
  @DisplayName("Deve notificar transferência realizada com sucesso")
  void deveNotificarTransferenciaRealizadaComSucesso() {
    // given
    ContextoTransferencia contexto = prepararTransferenciaComSucesso();

    // when
    transferenciaService.realizar(contexto.comando());

    // then
    ArgumentCaptor<TransferenciaRealizadaEvent> eventCaptor =
        ArgumentCaptor.forClass(TransferenciaRealizadaEvent.class);

    verify(notificacaoService).notificaTransferenciaCompleta(eventCaptor.capture());

    TransferenciaRealizadaEvent event = eventCaptor.getValue();

    assertThat(event.contaOrigemId()).isEqualTo(CONTA_ORIGEM_ID);
    assertThat(event.contaDestinoId()).isEqualTo(CONTA_DESTINO_ID);
    assertThat(event.valor()).isEqualByComparingTo("100.00");
    assertThat(event.realizadaEm()).isNotNull();
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("transferenciasInvalidas")
  @DisplayName("Deve rejeitar transferência inválida")
  void deveRejeitarTransferenciaInvalida(String cenario, RealizarTransferenciaCommand comando) {
    // given
    // cenário recebido pelo MethodSource

    // when
    assertThrows(
        TransferenciaInvalidaException.class, () -> transferenciaService.realizar(comando));

    verifyNoInteractions(contaRepository);
    verifyNoInteractions(transferenciaRepository);
    verifyNoInteractions(movimentacaoRepository);
    verifyNoInteractions(notificacaoService);
  }

  @Test
  @DisplayName("Deve rejeitar transferência quando conta origem não existir")
  void deveRejeitarTransferenciaQuandoContaOrigemNaoExistir() {
    // given
    RealizarTransferenciaCommand comando =
        new RealizarTransferenciaCommand(
            CONTA_ORIGEM_ID, CONTA_DESTINO_ID, new BigDecimal("100.00"));

    when(contaRepository.findByIdForUpdate(CONTA_ORIGEM_ID)).thenReturn(Optional.empty());

    // when/then
    assertThrows(ContaNaoEncontradaException.class, () -> transferenciaService.realizar(comando));

    verify(contaRepository).findByIdForUpdate(CONTA_ORIGEM_ID);
    verify(contaRepository, never()).findByIdForUpdate(CONTA_DESTINO_ID);

    verifyNoInteractions(transferenciaRepository);
    verifyNoInteractions(movimentacaoRepository);
    verifyNoInteractions(notificacaoService);
  }

  @Test
  @DisplayName("Deve rejeitar transferência quando conta destino não existir")
  void deveRejeitarTransferenciaQuandoContaDestinoNaoExistir() {
    // given
    ContaEntity contaOrigem = criarConta(CONTA_ORIGEM_ID, "JOÃO", "1000.00");

    RealizarTransferenciaCommand comando =
        new RealizarTransferenciaCommand(
            CONTA_ORIGEM_ID, CONTA_DESTINO_ID, new BigDecimal("100.00"));

    when(contaRepository.findByIdForUpdate(CONTA_ORIGEM_ID)).thenReturn(Optional.of(contaOrigem));

    when(contaRepository.findByIdForUpdate(CONTA_DESTINO_ID)).thenReturn(Optional.empty());

    // when/then
    assertThrows(ContaNaoEncontradaException.class, () -> transferenciaService.realizar(comando));

    verify(contaRepository).findByIdForUpdate(CONTA_ORIGEM_ID);
    verify(contaRepository).findByIdForUpdate(CONTA_DESTINO_ID);

    verifyNoInteractions(transferenciaRepository);
    verifyNoInteractions(movimentacaoRepository);
    verifyNoInteractions(notificacaoService);
  }

  @Test
  @DisplayName("Deve rejeitar transferência quando saldo for insuficiente")
  void deveRejeitarTransferenciaQuandoSaldoForInsuficiente() {
    // given
    ContaEntity contaOrigem = criarConta(CONTA_ORIGEM_ID, "JOÃO", "100.00");
    ContaEntity contaDestino = criarConta(CONTA_DESTINO_ID, "MARIA", "500.00");

    RealizarTransferenciaCommand comando =
        new RealizarTransferenciaCommand(
            CONTA_ORIGEM_ID, CONTA_DESTINO_ID, new BigDecimal("1000.00"));

    when(contaRepository.findByIdForUpdate(CONTA_ORIGEM_ID)).thenReturn(Optional.of(contaOrigem));

    when(contaRepository.findByIdForUpdate(CONTA_DESTINO_ID)).thenReturn(Optional.of(contaDestino));

    // when/then

    assertThrows(SaldoInsuficienteException.class, () -> transferenciaService.realizar(comando));

    assertThat(contaOrigem.getSaldo()).isEqualByComparingTo("100.00");
    assertThat(contaDestino.getSaldo()).isEqualByComparingTo("500.00");

    verify(contaRepository).findByIdForUpdate(CONTA_ORIGEM_ID);
    verify(contaRepository).findByIdForUpdate(CONTA_DESTINO_ID);

    verify(contaRepository, never()).save(any());
    verifyNoInteractions(transferenciaRepository);
    verifyNoInteractions(movimentacaoRepository);
    verifyNoInteractions(notificacaoService);
  }

  static Stream<Arguments> transferenciasInvalidas() {
    UUID origem = UUID.fromString("00000000-0000-0000-0000-000000000001");
    UUID destino = UUID.fromString("00000000-0000-0000-0000-000000000002");

    return Stream.of(
        Arguments.of(
            "Conta origem nula",
            new RealizarTransferenciaCommand(null, destino, new BigDecimal("100.00"))),
        Arguments.of(
            "Conta destino nula",
            new RealizarTransferenciaCommand(origem, null, new BigDecimal("100.00"))),
        Arguments.of(
            "Origem igual ao destino",
            new RealizarTransferenciaCommand(origem, origem, new BigDecimal("100.00"))),
        Arguments.of("Valor nulo", new RealizarTransferenciaCommand(origem, destino, null)),
        Arguments.of(
            "Valor zero", new RealizarTransferenciaCommand(origem, destino, BigDecimal.ZERO)),
        Arguments.of(
            "Valor negativo",
            new RealizarTransferenciaCommand(origem, destino, new BigDecimal("-10.00"))));
  }

  private ContaEntity criarConta(UUID id, String nome, String saldo) {
    return ContaEntity.builder().id(id).nome(nome).saldo(new BigDecimal(saldo)).build();
  }

  private ContextoTransferencia prepararTransferenciaComSucesso() {
    BigDecimal valor = new BigDecimal("100.00");

    ContaEntity contaOrigem = criarConta(CONTA_ORIGEM_ID, "João", "1000.00");
    ContaEntity contaDestino = criarConta(CONTA_DESTINO_ID, "Maria", "500.00");

    RealizarTransferenciaCommand comando =
        new RealizarTransferenciaCommand(CONTA_ORIGEM_ID, CONTA_DESTINO_ID, valor);

    when(contaRepository.findByIdForUpdate(CONTA_ORIGEM_ID)).thenReturn(Optional.of(contaOrigem));

    when(contaRepository.findByIdForUpdate(CONTA_DESTINO_ID)).thenReturn(Optional.of(contaDestino));

    when(transferenciaRepository.saveAndFlush(any(TransferenciaEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    when(movimentacaoRepository.saveAllAndFlush(anyList()))
        .thenAnswer(invocation -> invocation.getArgument(0));

    return new ContextoTransferencia(comando, contaOrigem, contaDestino);
  }

  private MovimentacaoEntity obterMovimentacaoPorTipo(
      List<MovimentacaoEntity> movimentacoes, MovimentacaoTipoEnum tipo) {
    return movimentacoes.stream()
        .filter(movimentacao -> movimentacao.getTipo() == tipo)
        .findFirst()
        .orElseThrow();
  }

  private record ContextoTransferencia(
      RealizarTransferenciaCommand comando, ContaEntity contaOrigem, ContaEntity contaDestino) {}
}
