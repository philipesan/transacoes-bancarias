CREATE TABLE movimentacao (
                              id UUID PRIMARY KEY,
                              conta_id UUID NOT NULL,
                              transferencia_id UUID NOT NULL,
                              tipo VARCHAR(20) NOT NULL,
                              valor NUMERIC(19, 2) NOT NULL,
                              saldo_apos NUMERIC(19, 2) NOT NULL,
                              realizada_em TIMESTAMP NOT NULL,

                              CONSTRAINT fk_movimentacao_conta
                                  FOREIGN KEY (conta_id)
                                      REFERENCES conta (id),

                              CONSTRAINT fk_movimentacao_transferencia
                                  FOREIGN KEY (transferencia_id)
                                      REFERENCES transferencia (id),

                              CONSTRAINT chk_movimentacao_tipo
                                  CHECK (tipo IN ('DEBITO', 'CREDITO')),

                              CONSTRAINT chk_movimentacao_valor_positivo
                                  CHECK (valor > 0),

                              CONSTRAINT chk_movimentacao_saldo_apos_nao_negativo
                                  CHECK (saldo_apos >= 0)
);

CREATE INDEX idx_movimentacao_conta
    ON movimentacao (conta_id);

CREATE INDEX idx_movimentacao_transferencia
    ON movimentacao (transferencia_id);

CREATE INDEX idx_movimentacao_conta_realizada_em
    ON movimentacao (conta_id, realizada_em DESC);