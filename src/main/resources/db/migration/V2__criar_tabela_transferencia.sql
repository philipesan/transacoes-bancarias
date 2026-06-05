CREATE TABLE transferencia (
                               id UUID PRIMARY KEY,
                               conta_origem_id UUID NOT NULL,
                               conta_destino_id UUID NOT NULL,
                               valor NUMERIC(19, 2) NOT NULL,
                               realizada_em TIMESTAMP NOT NULL,

                               CONSTRAINT fk_transferencia_conta_origem
                                   FOREIGN KEY (conta_origem_id)
                                       REFERENCES conta (id),

                               CONSTRAINT fk_transferencia_conta_destino
                                   FOREIGN KEY (conta_destino_id)
                                       REFERENCES conta (id),

                               CONSTRAINT chk_transferencia_valor_positivo
                                   CHECK (valor > 0),

                               CONSTRAINT chk_transferencia_contas_diferentes
                                   CHECK (conta_origem_id <> conta_destino_id)
);

CREATE INDEX idx_transferencia_conta_origem
    ON transferencia (conta_origem_id);

CREATE INDEX idx_transferencia_conta_destino
    ON transferencia (conta_destino_id);

CREATE INDEX idx_transferencia_realizada_em
    ON transferencia (realizada_em);