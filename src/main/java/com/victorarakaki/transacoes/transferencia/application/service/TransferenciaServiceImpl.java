package com.victorarakaki.transacoes.transferencia.application.service;

import com.victorarakaki.transacoes.conta.infrastructure.ContaEntity;
import com.victorarakaki.transacoes.conta.infrastructure.ContaRepository;
import com.victorarakaki.transacoes.notificacao.NotificacaoService;
import com.victorarakaki.transacoes.notificacao.TransferenciaRealizadaEvent;
import com.victorarakaki.transacoes.transferencia.application.Transferencia;
import com.victorarakaki.transacoes.transferencia.application.cqrs.command.RealizarTransferenciaCommand;
import com.victorarakaki.transacoes.transferencia.application.dto.ContasTransferencia;
import com.victorarakaki.transacoes.transferencia.application.exception.ContaNaoEncontradaException;
import com.victorarakaki.transacoes.transferencia.application.exception.SaldoInsuficienteException;
import com.victorarakaki.transacoes.transferencia.application.exception.TransferenciaInvalidaException;
import com.victorarakaki.transacoes.transferencia.infrastructure.TransferenciaEntity;
import com.victorarakaki.transacoes.transferencia.infrastructure.TransferenciaEntityMapper;
import com.victorarakaki.transacoes.transferencia.infrastructure.TransferenciaRepository;
import com.victorarakaki.transacoes.transferencia.movimentacao.application.MovimentacaoTipoEnum;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoEntity;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferenciaServiceImpl implements TransferenciaService {

  private final ContaRepository contaRepository;
  private final TransferenciaRepository transferenciaRepository;
  private final MovimentacaoRepository movimentacaoRepository;
  private final TransferenciaEntityMapper transferenciaEntityMapper;
  private final NotificacaoService notificacaoService;

  @Override
  @Transactional
  public Transferencia realizar(RealizarTransferenciaCommand comando) {
    log.info("Transferência: iniciando validação dos dados");

    validarTransferencia(comando);

    log.info("Transferência: buscando contas com lock pessimista");

    ContasTransferencia contas =
        buscarContasComLock(comando.contaOrigemId(), comando.contaDestinoId());

    ContaEntity contaOrigem = contas.contaOrigem();
    ContaEntity contaDestino = contas.contaDestino();

    validarSaldoSuficiente(contaOrigem, comando.valor());

    log.info("Transferência: debitando conta origem e creditando conta destino");

    contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(comando.valor()));
    contaDestino.setSaldo(contaDestino.getSaldo().add(comando.valor()));

    contaRepository.save(contaOrigem);
    contaRepository.save(contaDestino);

    LocalDateTime realizadaEm = LocalDateTime.now();

    TransferenciaEntity transferenciaEntity =
        TransferenciaEntity.builder()
            .id(UUID.randomUUID())
            .contaOrigem(contaOrigem)
            .contaDestino(contaDestino)
            .valor(comando.valor())
            .realizadaEm(realizadaEm)
            .build();

    transferenciaEntity = transferenciaRepository.saveAndFlush(transferenciaEntity);

    MovimentacaoEntity debito =
        MovimentacaoEntity.builder()
            .id(UUID.randomUUID())
            .conta(contaOrigem)
            .transferencia(transferenciaEntity)
            .tipo(MovimentacaoTipoEnum.DEBITO)
            .valor(comando.valor())
            .saldoApos(contaOrigem.getSaldo())
            .realizadaEm(realizadaEm)
            .build();

    MovimentacaoEntity credito =
        MovimentacaoEntity.builder()
            .id(UUID.randomUUID())
            .conta(contaDestino)
            .transferencia(transferenciaEntity)
            .tipo(MovimentacaoTipoEnum.CREDITO)
            .valor(comando.valor())
            .saldoApos(contaDestino.getSaldo())
            .realizadaEm(realizadaEm)
            .build();

    movimentacaoRepository.saveAllAndFlush(List.of(debito, credito));

    notificacaoService.notificaTransferenciaCompleta(
        new TransferenciaRealizadaEvent(
            contaOrigem.getId(), contaDestino.getId(), comando.valor(), realizadaEm));

    log.info("Transferência: realizada com sucesso. ID: {}", transferenciaEntity.getId());

    return transferenciaEntityMapper.deEntity(transferenciaEntity);
  }

  private void validarTransferencia(RealizarTransferenciaCommand comando) {
    if (Objects.isNull(comando.contaOrigemId())) {
      throw new TransferenciaInvalidaException("Conta de origem é obrigatória.");
    }

    if (Objects.isNull(comando.contaDestinoId())) {
      throw new TransferenciaInvalidaException("Conta de destino é obrigatória.");
    }

    if (comando.contaOrigemId().equals(comando.contaDestinoId())) {
      throw new TransferenciaInvalidaException(
          "Conta de origem e conta de destino devem ser diferentes.");
    }

    if (Objects.isNull(comando.valor()) || comando.valor().compareTo(BigDecimal.ZERO) <= 0) {
      throw new TransferenciaInvalidaException("Valor da transferência deve ser positivo.");
    }
  }

  private void validarSaldoSuficiente(ContaEntity contaOrigem, BigDecimal valor) {
    if (contaOrigem.getSaldo().compareTo(valor) < 0) {
      throw new SaldoInsuficienteException("Saldo insuficiente para realizar a transferência.");
    }
  }

  private ContasTransferencia buscarContasComLock(UUID contaOrigemId, UUID contaDestinoId) {
    List<UUID> contasOrdenadas =
        Stream.of(contaOrigemId, contaDestinoId).sorted(Comparator.naturalOrder()).toList();

    ContaEntity primeiraConta = buscarContaComLock(contasOrdenadas.get(0));
    ContaEntity segundaConta = buscarContaComLock(contasOrdenadas.get(1));

    ContaEntity contaOrigem =
        primeiraConta.getId().equals(contaOrigemId) ? primeiraConta : segundaConta;

    ContaEntity contaDestino =
        primeiraConta.getId().equals(contaDestinoId) ? primeiraConta : segundaConta;

    return new ContasTransferencia(contaOrigem, contaDestino);
  }

  private ContaEntity buscarContaComLock(UUID contaId) {
    return contaRepository
        .findByIdForUpdate(contaId)
        .orElseThrow(() -> new ContaNaoEncontradaException(contaId));
  }
}
