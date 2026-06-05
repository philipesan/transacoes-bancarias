CREATE TABLE conta (
                       id UUID PRIMARY KEY,
                       nome VARCHAR(150) NOT NULL,
                       saldo NUMERIC(19, 2) NOT NULL,

                       CONSTRAINT chk_conta_saldo_nao_negativo
                           CHECK (saldo >= 0)
);