package com.victorarakaki.transacoes.conta.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.victorarakaki.transacoes.IntegrationTestBase;
import com.victorarakaki.transacoes.conta.api.request.CriarContaRequestDTO;
import com.victorarakaki.transacoes.conta.infrastructure.ContaEntity;
import com.victorarakaki.transacoes.conta.infrastructure.ContaRepository;
import com.victorarakaki.transacoes.transferencia.infrastructure.TransferenciaEntity;
import com.victorarakaki.transacoes.transferencia.infrastructure.TransferenciaRepository;
import com.victorarakaki.transacoes.transferencia.movimentacao.application.MovimentacaoTipoEnum;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoEntity;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
class ContaControllerIntegrationTest extends IntegrationTestBase {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

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
  @DisplayName("Deve criar conta com sucesso")
  void deveCriarContaComSucesso() throws Exception {
    // given
    CriarContaRequestDTO request =
        new CriarContaRequestDTO("João Silva", new BigDecimal("1000.00"));

    // when / then
    mockMvc
        .perform(
            post("/contas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.nome").value("JOÃO SILVA"))
        .andExpect(jsonPath("$.saldoInicial").value(1000.00));

    List<ContaEntity> contas = contaRepository.findAll();

    assertThat(contas).hasSize(1);

    ContaEntity contaSalva = contas.getFirst();

    assertThat(contaSalva.getId()).isNotNull();
    assertThat(contaSalva.getNome()).isEqualTo("JOÃO SILVA");
    assertThat(contaSalva.getSaldo()).isEqualByComparingTo("1000.00");
  }

  @Test
  @DisplayName("Deve consultar movimentações de uma conta")
  void deveConsultarMovimentacoesDeUmaConta() throws Exception {
    // given
    ContaEntity contaOrigem =
        contaRepository.saveAndFlush(
            ContaEntity.builder()
                .id(UUID.randomUUID())
                .nome("Conta Origem")
                .saldo(new BigDecimal("900.00"))
                .build());

    ContaEntity contaDestino =
        contaRepository.saveAndFlush(
            ContaEntity.builder()
                .id(UUID.randomUUID())
                .nome("Conta Destino")
                .saldo(new BigDecimal("600.00"))
                .build());

    LocalDateTime realizadaEm = LocalDateTime.now();

    TransferenciaEntity transferencia =
        transferenciaRepository.saveAndFlush(
            TransferenciaEntity.builder()
                .id(UUID.randomUUID())
                .contaOrigem(contaOrigem)
                .contaDestino(contaDestino)
                .valor(new BigDecimal("100.00"))
                .realizadaEm(realizadaEm)
                .build());

    movimentacaoRepository.saveAndFlush(
        MovimentacaoEntity.builder()
            .id(UUID.randomUUID())
            .conta(contaOrigem)
            .transferencia(transferencia)
            .tipo(MovimentacaoTipoEnum.DEBITO)
            .valor(new BigDecimal("100.00"))
            .saldoApos(new BigDecimal("900.00"))
            .realizadaEm(realizadaEm)
            .build());

    // when / then
    mockMvc
        .perform(get("/contas/{contaId}/movimentacoes", contaOrigem.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].contaId").value(contaOrigem.getId().toString()))
        .andExpect(jsonPath("$[0].transferenciaId").value(transferencia.getId().toString()))
        .andExpect(jsonPath("$[0].tipo").value("DEBITO"))
        .andExpect(jsonPath("$[0].valor").value(100.00))
        .andExpect(jsonPath("$[0].saldoApos").value(900.00))
        .andExpect(jsonPath("$[0].realizadaEm").exists());
  }
}
