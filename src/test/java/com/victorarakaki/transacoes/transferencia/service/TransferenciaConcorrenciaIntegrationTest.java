package com.victorarakaki.transacoes.transferencia.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.victorarakaki.transacoes.IntegrationTestBase;
import com.victorarakaki.transacoes.conta.infrastructure.ContaEntity;
import com.victorarakaki.transacoes.conta.infrastructure.ContaRepository;
import com.victorarakaki.transacoes.transferencia.application.cqrs.command.RealizarTransferenciaCommand;
import com.victorarakaki.transacoes.transferencia.application.service.TransferenciaService;
import com.victorarakaki.transacoes.transferencia.infrastructure.TransferenciaRepository;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Tag("integration")
@SpringBootTest
class TransferenciaConcorrenciaIntegrationTest extends IntegrationTestBase {

  @Autowired private TransferenciaService transferenciaService;

  @Autowired private ContaRepository contaRepository;

  @Autowired private TransferenciaRepository transferenciaRepository;

  @Autowired private MovimentacaoRepository movimentacaoRepository;

  @BeforeEach
  void limparBanco() {
    movimentacaoRepository.deleteAll();
    transferenciaRepository.deleteAll();
    contaRepository.deleteAll();
  }

  @Test
  @DisplayName("Deve processar transferências concorrentes mantendo saldo consistente")
  void deveProcessarTransferenciasConcorrentesMantendoSaldoConsistente() throws Exception {
    // given
    ContaEntity contaOrigem =
        contaRepository.saveAndFlush(
            ContaEntity.builder()
                .id(UUID.randomUUID())
                .nome("Conta Origem")
                .saldo(new BigDecimal("1000.00"))
                .build());

    ContaEntity contaDestino =
        contaRepository.saveAndFlush(
            ContaEntity.builder()
                .id(UUID.randomUUID())
                .nome("Conta Destino")
                .saldo(BigDecimal.ZERO)
                .build());

    int quantidadeTransferencias = 20;
    BigDecimal valorTransferencia = new BigDecimal("10.00");

    CountDownLatch inicioSimultaneo = new CountDownLatch(1);

    List<Callable<Boolean>> tarefas =
        IntStream.range(0, quantidadeTransferencias)
            .mapToObj(i -> (Callable<Boolean>) () -> {
                          inicioSimultaneo.await();
                          transferenciaService.realizar(
                              new RealizarTransferenciaCommand(
                                  contaOrigem.getId(), contaDestino.getId(), valorTransferencia));
                          return true;})
            .toList();

    // when
    List<Future<Boolean>> futuros;

    try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
      futuros = tarefas.stream().map(executorService::submit).toList();

      inicioSimultaneo.countDown();

      for (Future<Boolean> futuro : futuros) {
        assertThat(futuro.get(20, TimeUnit.SECONDS)).isTrue();
      }
    }

    // then
    ContaEntity origemAtualizada = contaRepository.findById(contaOrigem.getId()).orElseThrow();

    ContaEntity destinoAtualizado = contaRepository.findById(contaDestino.getId()).orElseThrow();

    assertThat(origemAtualizada.getSaldo()).isEqualByComparingTo("800.00");
    assertThat(destinoAtualizado.getSaldo()).isEqualByComparingTo("200.00");

    assertThat(transferenciaRepository.count()).isEqualTo(20);
    assertThat(movimentacaoRepository.count()).isEqualTo(40);
  }

    @Test
    @DisplayName("Deve processar transferências cruzadas concorrentes sem deadlock")
    void deveProcessarTransferenciasCruzadasConcorrentesSemDeadlock() throws Exception {
        // given
        ContaEntity contaA = contaRepository.saveAndFlush(
                ContaEntity.builder()
                        .id(UUID.randomUUID())
                        .nome("Conta A")
                        .saldo(new BigDecimal("1000.00"))
                        .build()
        );

        ContaEntity contaB = contaRepository.saveAndFlush(
                ContaEntity.builder()
                        .id(UUID.randomUUID())
                        .nome("Conta B")
                        .saldo(new BigDecimal("1000.00"))
                        .build()
        );

        int quantidadePorSentido = 20;
        BigDecimal valorTransferencia = new BigDecimal("10.00");

        CountDownLatch inicioSimultaneo = new CountDownLatch(1);

        List<Callable<Boolean>> tarefasAparaB = IntStream.range(0, quantidadePorSentido)
                .mapToObj(i -> (Callable<Boolean>) () -> {
                    inicioSimultaneo.await();

                    transferenciaService.realizar(
                            new RealizarTransferenciaCommand(
                                    contaA.getId(),
                                    contaB.getId(),
                                    valorTransferencia
                            )
                    );

                    return true;
                })
                .toList();

        List<Callable<Boolean>> tarefasBparaA = IntStream.range(0, quantidadePorSentido)
                .mapToObj(i -> (Callable<Boolean>) () -> {
                    inicioSimultaneo.await();

                    transferenciaService.realizar(
                            new RealizarTransferenciaCommand(
                                    contaB.getId(),
                                    contaA.getId(),
                                    valorTransferencia
                            )
                    );

                    return true;
                })
                .toList();

        List<Callable<Boolean>> tarefas = Stream.concat(
                        tarefasAparaB.stream(),
                        tarefasBparaA.stream()
                )
                .toList();

        // when
        List<Future<Boolean>> futuros;

        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            futuros = tarefas.stream()
                    .map(executorService::submit)
                    .toList();

            inicioSimultaneo.countDown();

            for (Future<Boolean> futuro : futuros) {
                assertThat(futuro.get(30, TimeUnit.SECONDS)).isTrue();
            }
        }

        // then
        ContaEntity contaAAtualizada = contaRepository.findById(contaA.getId())
                .orElseThrow();

        ContaEntity contaBAtualizada = contaRepository.findById(contaB.getId())
                .orElseThrow();

        assertThat(contaAAtualizada.getSaldo()).isEqualByComparingTo("1000.00");
        assertThat(contaBAtualizada.getSaldo()).isEqualByComparingTo("1000.00");

        assertThat(transferenciaRepository.count()).isEqualTo(40);
        assertThat(movimentacaoRepository.count()).isEqualTo(80);
    }
}
