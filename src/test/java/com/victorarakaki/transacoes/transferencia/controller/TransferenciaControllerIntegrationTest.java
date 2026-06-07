package com.victorarakaki.transacoes.transferencia.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.victorarakaki.transacoes.IntegrationTestBase;
import com.victorarakaki.transacoes.conta.infrastructure.ContaEntity;
import com.victorarakaki.transacoes.conta.infrastructure.ContaRepository;
import com.victorarakaki.transacoes.notificacao.NotificacaoService;
import com.victorarakaki.transacoes.notificacao.TransferenciaRealizadaEvent;
import com.victorarakaki.transacoes.transferencia.api.request.TransferenciaRequestDTO;
import com.victorarakaki.transacoes.transferencia.infrastructure.TransferenciaEntity;
import com.victorarakaki.transacoes.transferencia.infrastructure.TransferenciaRepository;
import com.victorarakaki.transacoes.transferencia.movimentacao.application.MovimentacaoTipoEnum;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoEntity;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
class TransferenciaControllerIntegrationTest extends IntegrationTestBase {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ContaRepository contaRepository;

  @Autowired private TransferenciaRepository transferenciaRepository;

  @MockitoSpyBean private MovimentacaoRepository movimentacaoRepository;

  @MockitoBean private NotificacaoService notificacaoService;

  @BeforeEach
  void limparBanco() {
    movimentacaoRepository.deleteAll();
    transferenciaRepository.deleteAll();
    contaRepository.deleteAll();
  }

  @Test
  @DisplayName("Deve realizar transferência com sucesso")
  void deveRealizarTransferenciaComSucesso() throws Exception {
    // given
    ContaEntity contaOrigem =
        contaRepository.saveAndFlush(
            ContaEntity.builder()
                .id(UUID.randomUUID())
                .nome("JOÃO SILVA")
                .saldo(new BigDecimal("1000.00"))
                .build());

    ContaEntity contaDestino =
        contaRepository.saveAndFlush(
            ContaEntity.builder()
                .id(UUID.randomUUID())
                .nome("MARIA SOUZA")
                .saldo(new BigDecimal("500.00"))
                .build());

    TransferenciaRequestDTO request =
        new TransferenciaRequestDTO(
            contaOrigem.getId(), contaDestino.getId(), new BigDecimal("100.00"));

    // when
    mockMvc
        .perform(
            post("/transferencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.contaOrigemId").value(contaOrigem.getId().toString()))
        .andExpect(jsonPath("$.contaDestinoId").value(contaDestino.getId().toString()))
        .andExpect(jsonPath("$.valor").value(100.00))
        .andExpect(jsonPath("$.realizadaEm").exists());

    ContaEntity origemAtualizada = contaRepository.findById(contaOrigem.getId()).orElseThrow();

    ContaEntity destinoAtualizado = contaRepository.findById(contaDestino.getId()).orElseThrow();

    assertThat(origemAtualizada.getSaldo()).isEqualByComparingTo("900.00");
    assertThat(destinoAtualizado.getSaldo()).isEqualByComparingTo("600.00");

    List<TransferenciaEntity> transferencias = transferenciaRepository.findAll();

    assertThat(transferencias)
        .anySatisfy(
            transferencia -> {
              assertThat(transferencia.getContaOrigem().getId()).isEqualTo(contaOrigem.getId());
              assertThat(transferencia.getContaDestino().getId()).isEqualTo(contaDestino.getId());
              assertThat(transferencia.getValor()).isEqualByComparingTo("100.00");
              assertThat(transferencia.getRealizadaEm()).isNotNull();
            });

    List<MovimentacaoEntity> movimentacoesOrigem =
        movimentacaoRepository.findByConta_IdOrderByRealizadaEmDesc(contaOrigem.getId());

    List<MovimentacaoEntity> movimentacoesDestino =
        movimentacaoRepository.findByConta_IdOrderByRealizadaEmDesc(contaDestino.getId());

    assertThat(movimentacoesOrigem).hasSize(1);
    assertThat(movimentacoesDestino).hasSize(1);

    MovimentacaoEntity debito = movimentacoesOrigem.getFirst();
    MovimentacaoEntity credito = movimentacoesDestino.getFirst();

    assertThat(debito.getTipo()).isEqualTo(MovimentacaoTipoEnum.DEBITO);
    assertThat(debito.getValor()).isEqualByComparingTo("100.00");
    assertThat(debito.getSaldoApos()).isEqualByComparingTo("900.00");

    assertThat(credito.getTipo()).isEqualTo(MovimentacaoTipoEnum.CREDITO);
    assertThat(credito.getValor()).isEqualByComparingTo("100.00");
    assertThat(credito.getSaldoApos()).isEqualByComparingTo("600.00");
  }

  @Test
  @DisplayName("Deve dar rollback na transferência em caso de erro e não enviar notificação")
  void deveDarRollbackNaTransferenciaEmCasoDeErroENaoEnviarNotificacao() throws Exception {
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
                .saldo(new BigDecimal("500.00"))
                .build());

    TransferenciaRequestDTO request =
        new TransferenciaRequestDTO(
            contaOrigem.getId(), contaDestino.getId(), new BigDecimal("100.00"));

    doThrow(new RuntimeException("Falha simulada ao salvar movimentações"))
        .when(movimentacaoRepository)
        .saveAllAndFlush(anyList());

    // when / then
    mockMvc
        .perform(
            post("/transferencias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.type").value("/problemas/erro-interno"))
        .andExpect(jsonPath("$.codigo").value("ERRO_INTERNO"));

    ContaEntity origemAtualizada = contaRepository.findById(contaOrigem.getId()).orElseThrow();

    ContaEntity destinoAtualizado = contaRepository.findById(contaDestino.getId()).orElseThrow();

    assertThat(origemAtualizada.getSaldo()).isEqualByComparingTo("1000.00");
    assertThat(destinoAtualizado.getSaldo()).isEqualByComparingTo("500.00");

    assertThat(transferenciaRepository.count()).isZero();

    verify(notificacaoService, never())
        .notificaTransferenciaCompleta(any(TransferenciaRealizadaEvent.class));
  }
}
