package com.victorarakaki.transacoes.conta.service;

import com.victorarakaki.transacoes.conta.application.Conta;
import com.victorarakaki.transacoes.conta.application.cqrs.command.CriarContaCommand;
import com.victorarakaki.transacoes.conta.application.exception.CriarContaSaldoInvalidoException;
import com.victorarakaki.transacoes.conta.application.exception.NomeInvalidoException;
import com.victorarakaki.transacoes.conta.application.service.ContaServiceImpl;
import com.victorarakaki.transacoes.conta.infrastructure.ContaEntity;
import com.victorarakaki.transacoes.conta.infrastructure.ContaEntityMapper;
import com.victorarakaki.transacoes.conta.infrastructure.ContaRepository;
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

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
class ContaServiceUnitTest {
    @Mock
    private ContaRepository contaRepository;

    @Spy
    private ContaEntityMapper contaEntityMapper =
            Mappers.getMapper(ContaEntityMapper.class);

    @InjectMocks
    private ContaServiceImpl contaService;

    @Test
    @DisplayName("Deve criar conta com sucesso")
    void deveCriarContaComSucesso() {
        // given
        CriarContaCommand comando = new CriarContaCommand(
                "João Silva",
                new BigDecimal("1000.00")
        );

        when(contaRepository.saveAndFlush(any(ContaEntity.class)))
                .thenAnswer(invocation -> {
                    ContaEntity conta = invocation.getArgument(0);

                    if (conta.getId() == null) {
                        conta.setId(UUID.randomUUID());
                    }

                    return conta;
                });

        // when
        Conta resultado = contaService.criar(comando);

        // then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("JOÃO SILVA");
        assertThat(resultado.getSaldo()).isEqualByComparingTo("1000.00");

        ArgumentCaptor<ContaEntity> contaCaptor =
                ArgumentCaptor.forClass(ContaEntity.class);

        verify(contaRepository).saveAndFlush(contaCaptor.capture());

        ContaEntity contaSalva = contaCaptor.getValue();

        assertThat(contaSalva.getNome()).isEqualTo("JOÃO SILVA");
        assertThat(contaSalva.getSaldo()).isEqualByComparingTo("1000.00");

        verifyNoMoreInteractions(contaRepository);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("nomesInvalidos")
    @DisplayName("Deve rejeitar criação de conta com nome inválido")
    void deveRejeitarCriacaoDeContaComNomeInvalido(
            String cenario,
            String nome
    ) {
        // given
        CriarContaCommand comando = new CriarContaCommand(
                nome,
                new BigDecimal("1000.00")
        );

        // when / then
        assertThrows(
                NomeInvalidoException.class,
                () -> contaService.criar(comando)
        );

        verifyNoInteractions(contaRepository);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("saldosInvalidos")
    @DisplayName("Deve rejeitar criação de conta com saldo inicial inválido")
    void deveRejeitarCriacaoDeContaComSaldoInicialInvalido(
            String cenario,
            BigDecimal saldoInicial
    ) {
        // given
        CriarContaCommand comando = new CriarContaCommand(
                "João Silva",
                saldoInicial
        );

        // when / then
        assertThrows(
                CriarContaSaldoInvalidoException.class,
                () -> contaService.criar(comando)
        );

        verifyNoInteractions(contaRepository);
    }

    static Stream<Arguments> nomesInvalidos() {
        return Stream.of(
                Arguments.of("Nome nulo", null),
                Arguments.of("Nome vazio", ""),
                Arguments.of("Nome em branco", "   ")
        );
    }

    static Stream<Arguments> saldosInvalidos() {
        return Stream.of(
                Arguments.of("Saldo inicial nulo", null),
                Arguments.of("Saldo inicial negativo", new BigDecimal("-1.00"))
        );
    }
}

