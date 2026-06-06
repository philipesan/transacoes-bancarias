package com.victorarakaki.transacoes.transferencia.movimentacao.application.service;

import com.victorarakaki.transacoes.conta.infrastructure.ContaRepository;
import com.victorarakaki.transacoes.transferencia.application.exception.ContaNaoEncontradaException;
import com.victorarakaki.transacoes.transferencia.application.exception.TransferenciaInvalidaException;
import com.victorarakaki.transacoes.transferencia.movimentacao.application.Movimentacao;
import com.victorarakaki.transacoes.transferencia.movimentacao.application.cqrs.query.ConsultarMovimentacoesQuery;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoEntityMapper;
import com.victorarakaki.transacoes.transferencia.movimentacao.infrastructure.MovimentacaoRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovimentacaoServiceImpl implements MovimentacaoService {

    private final ContaRepository contaRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final MovimentacaoEntityMapper movimentacaoEntityMapper;

    @Transactional(readOnly = true)
    @Override
    public List<Movimentacao> consultarMovimentacoes(ConsultarMovimentacoesQuery consulta) {
        log.info("Consulta de movimentações: validando conta {}", consulta.contaId());

        if (Objects.isNull(consulta.contaId())) {
            throw new TransferenciaInvalidaException("Conta é obrigatória para consultar movimentações.");
        }

        if (!contaRepository.existsById(consulta.contaId())) {
            throw new ContaNaoEncontradaException(consulta.contaId());
        }

        log.info("Consulta de movimentações: buscando movimentações da conta {}", consulta.contaId());

        return movimentacaoRepository.findByConta_IdOrderByRealizadaEmDesc(consulta.contaId())
                .stream()
                .map(movimentacaoEntityMapper::deEntity)
                .toList();
    }

}
