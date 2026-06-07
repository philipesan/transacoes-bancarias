package com.victorarakaki.transacoes;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@ActiveProfiles("test")
public abstract class IntegrationTestBase {

  @ServiceConnection
  protected static final PostgreSQLContainer POSTGRE =
      new PostgreSQLContainer()
          .withDatabaseName("transacao_bancaria_test")
          .withUsername("transacao_bancaria")
          .withPassword("transacao_bancaria_senha");

    static {
        POSTGRE.start();
    }

    private static @NonNull DockerImageName getImagem() {
        var imagem = "postgres:16-alpine";
        return DockerImageName.parse(imagem);
    }
}