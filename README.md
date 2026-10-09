#  API Rest - E-Commerce simplificado. Cadastro de produtos e gestão de pedidos.

### Descrição

---

##  Sobre o projeto
API REST desenvolvida em Java com Spring Boot para gerenciamento de clientes, produtos e pedidos. O projeto tem como objetivo praticar conceitos fundamentais de desenvolvimento backend, incluindo regras de negócio, autenticação, autorização e testes automatizados.

O README será atualizado conforme o projeto for progredindo.

---

## Tecnologias propostas

- Java 17+
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL ou H2
- Maven
- JUnit 5 + Mockito
- Bean Validation

---

## Funcionalidades

- Cadastro e gerenciamento de usuários.
- Cadastro, consulta e gerenciamento de produtos.
- Criação e consulta de pedidos.
- Controle de estoque.
- Autenticação e autorização por perfil.
- Tratamento padronizado de exceções.
- Testes automatizados das regras de negócio.

---

## Principais regras de negócio

- O e-mail do usuário deve ser único.
- Senhas devem ser armazenadas com criptografia apropriada, utilizando BCrypt.
- Produtos devem possuir preço positivo e estoque não negativo.
- Produtos inativos não podem ser incluídos em novos pedidos.
- O preço dos itens deve ser registrado no momento da criação do pedido.
- O estoque deve ser atualizado conforme as regras de confirmação do pedido, impedindo vendas acima da quantidade disponível.
- Pedidos devem seguir transições de status válidas: CREATED, CONFIRMED, CANCELLED e COMPLETED.
- Clientes só podem consultar os próprios pedidos.
- Apenas usuários autorizados podem acessar funcionalidades administrativas.
- Operações críticas devem utilizar transações para preservar a consistência dos dados.

---

## Objetivo do desafio

Desenvolver uma API organizada, segura e testável, aplicando boas práticas de Java e Spring Boot, com foco na implementação correta das regras de negócio e na qualidade do código.

Evolução proposta

Após concluir a versão inicial, o projeto poderá evoluir com controle de concorrência, idempotência, integração com serviços externos, mensageria com RabbitMQ, Docker e pipeline de CI/CD.