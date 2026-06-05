# transacoes-bancarias
Aplicação simples que simula transações bancárias


# Escolhas Arquiteturais/Design

## Gerais

### Utilização da Estrutura quebrada em "Pseudomódulos" representando dominios de negócio
**Escolha:** Segregar o projeto em pacotes representando o domínio de negócios de forma abstrata, com as camadas de API, Dominio e Infrastrutura dentro.

**Trade-off:** Para o escopo do projeto e timebox para execução, a escolha foi realizada para garantir a entrega do escopo no prazo, utilizando uma arquitetura que garanta o minimo de robusteza, manutenbilidade e desacoplamento, sendo facilmente adaptavel para um modulito modular ou uma arquitetura hexagonal se desejável.

### Utilização de CQRS como padrão de contrato para as camadas de dominio
**Escolha:** Utilizar objetos com responsabilidades segregadas entre comando e consulta para interagir com a camada de dominio, para não criar acoplamento da camada de API no Dominio da aplicação.

**Trade-off:** Pequeno aumento na complexidade e execução de um método da classe mapper para garantir o desacoplamento de camadas e integridade do dominio.

#### Retornar o objeto de dominio ao invés do entity.
**Escolha:** Services retornam um modelo de domínio, evitando expor a entidade JPA para a camada de API.

**Trade-off:** Isso mantém a persistência encapsulada e permite reaproveitar o retorno em outros fluxos.

#### Realizar validações na camada de Controller e na camada de Service.
**Escolha:** Validações rápidas do objeto de comando para garantir que a conta é valida, caso a classe seja reaproveitada e chamada fora de um controller validade.

**Trade-off:** Duplicação das validações, mas ganho substancial na resiliência da aplicação em caso de reaproveitamento da classe service.


## Módulo Conta

### Criação de Conta

#### Utilizar SaveAndFlush() ao salvar entidade.
**Escolha:** O saveAndFlush é usado para antecipar a validação das constraints do banco, permitindo que violações de integridade sejam capturadas e tratadas pelo handler global.

**Trade-off:** Assume diretamente o controle da transação, ao invés de delegar o controle ao Spring, neste caso, não é totalmente necessário pois não há segregação de camada entre operação de banco e regra de negócio.

## Módulo Notificação

#### Utilizar um objeto Event como padrão de contrato com o serviço de notificação
**Escolha:** O Serviço de notificação recebe este objeto com o ID das contas e o valor da transação
o método de notificação se responsabiliza por buscar os nomes e forma de contato dos correntistas para enviar a notificaçao, ele precisa apenas ficar sabendo que a transação ocorreu.

**Trade-off:** Nenhum trade-off negativo, entretanto, é importante para manter a segregação de responsabilidades dos módulos integra, garantindo que o método de notificação pudesse ser abstraído de forma segura sem expor objetos de dominio ou entidades a módulos externos.
