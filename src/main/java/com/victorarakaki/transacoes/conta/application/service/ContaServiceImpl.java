package com.victorarakaki.transacoes.conta.application.service;

import com.victorarakaki.transacoes.conta.application.Conta;
import com.victorarakaki.transacoes.conta.application.cqrs.command.CriarContaCommand;
import com.victorarakaki.transacoes.conta.application.exception.NomeInvalidoException;
import com.victorarakaki.transacoes.conta.infrastructure.ContaEntity;
import com.victorarakaki.transacoes.conta.infrastructure.ContaEntityMapper;
import com.victorarakaki.transacoes.conta.infrastructure.ContaRepository;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContaServiceImpl implements ContaService {

  private final ContaEntityMapper contaEntityMapper;
  private final ContaRepository contaRepository;

  @Override
  @Transactional
  public Conta criar(CriarContaCommand comando) {

    log.info("Iniciando criação de conta");
    validaCriacaoUsuario(comando);

    log.debug("Criação de conta: Dados da conta: " + comando);
    ContaEntity contaEntity =
        ContaEntity.builder()
            .id(UUID.randomUUID())
            .nome(comando.nome().trim().toUpperCase())
            .saldo(comando.saldoInicial())
            .build();

    contaEntity = contaRepository.saveAndFlush(contaEntity);

    log.debug("Criação de conta: conta criada, ID %s".formatted(contaEntity.getId()));

    return contaEntityMapper.deEntity(contaEntity);
  }

  private void validaCriacaoUsuario(CriarContaCommand comando) {
    log.info("Criação de conta: Validando dados");
    if (Objects.isNull(comando.nome()) || comando.nome().isBlank()) {
      throw new NomeInvalidoException("Nome de usuário Inválido");
    }

    if (Objects.isNull(comando.saldoInicial())
        || comando.saldoInicial().compareTo(BigDecimal.ZERO) < 0) {
      throw new NomeInvalidoException("Tentativa de Criar conta com saldo negativo ou nulo");
    }

    log.info("Criação de conta: Dados válidos");
  }
}
