# 🎲 MoaBet API

Simulador de apostas esportivas desenvolvido em Spring Boot, com autenticação JWT, controle de acesso baseado em papéis (RBAC) e gerenciamento completo de usuários, carteiras, eventos e apostas.

> Projeto de estudo com foco em boas práticas de backend: arquitetura em camadas, validação, segurança e tratamento de erros.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-blue?logo=springsecurity&logoColor=white)
![H2](https://img.shields.io/badge/Database-H2-lightgrey?logo=h2&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/license-MIT-yellow)

---

## 📌 Sobre o projeto

O **MoaBet** é uma API REST que simula uma casa de apostas: usuários se cadastram, depositam saldo em uma carteira virtual, e apostam em eventos com odds definidas. Administradores criam e finalizam eventos, resolvendo automaticamente todas as apostas pendentes (ganhas ou perdidas) com base no resultado.

O projeto foi construído com foco em **arquitetura sólida e segurança real**, não só em "fazer funcionar":

- Camadas bem definidas (`Controller → Service → Repository`)
- DTOs separados de entidades (nunca expõe o model direto)
- Autenticação via **JWT** e autorização via **Roles (ADMIN/USER)**
- Prevenção de **IDOR** (Insecure Direct Object Reference) — nenhum endpoint confia em IDs vindos do client sem validar contra o usuário autenticado
- Uso de **BigDecimal** para todos os valores monetários (evita erros de arredondamento de ponto flutuante)
- Validação de entrada com **Bean Validation**, com handler global devolvendo todos os erros de uma vez
- Tratamento de erro centralizado (`@ControllerAdvice`) para exceções de negócio, autenticação e autorização

---

## 🧱 Arquitetura

```
Controller → Service → Repository
```

- **Controller**: recebe a requisição HTTP, aplica `@Valid` e `@PreAuthorize`, delega pro Service.
- **Service**: contém toda a regra de negócio (validações de domínio, cálculos, orquestração entre repositórios).
- **Repository**: acesso a dados via Spring Data JPA.
- **DTOs**: um conjunto de request/response por recurso, nunca a entidade JPA é exposta na API.

### Entidades principais

| Entidade      | Descrição                                                                |
| ------------- | ------------------------------------------------------------------------ |
| `User`        | Usuário do sistema, com role (`ADMIN`/`USER`)                            |
| `Wallet`      | Carteira do usuário (relação 1:1 com `User`), guarda o saldo             |
| `Transaction` | Histórico de movimentações (`DEPOSIT`, `WITHDRAWAL`, `BET`, `WIN`)       |
| `Event`       | Evento apostável, com odds e status (`OPEN`, `CLOSED`, `FINISHED`)       |
| `Bet`         | Aposta de um usuário em um evento, com status (`PENDING`, `WON`, `LOST`) |

---

## 🔐 Segurança

- **Autenticação:** login gera um token JWT (expiração de 2h), assinado com HMAC-SHA.
- **Autorização:** roles `ADMIN` e `USER`, aplicadas via `@PreAuthorize` em cada endpoint.
- **Proteção contra IDOR:**
  - O `userId` de uma aposta nunca vem do corpo da requisição — é extraído do usuário autenticado (`SecurityContextHolder`).
  - Endpoints como "ver perfil" ou "depositar" validam, via SpEL (`@PreAuthorize`), que o solicitante é o **dono do recurso** ou um **admin** — nunca aceitando o ID de outra pessoa.
- **Senhas:** hash com BCrypt, nunca armazenadas em texto puro.

---

## 🚀 Endpoints

### Auth (`/api/auth`)

| Método | Rota        | Acesso  |
| ------ | ----------- | ------- |
| POST   | `/register` | Público |
| POST   | `/login`    | Público |

### Users (`/api/users`)

| Método | Rota          | Acesso                     |
| ------ | ------------- | -------------------------- |
| GET    | `/users`      | ADMIN                      |
| GET    | `/users/{id}` | ADMIN ou o próprio usuário |
| DELETE | `/users/{id}` | ADMIN ou o próprio usuário |

### Events (`/api/events`)

| Método | Rota                  | Acesso      |
| ------ | --------------------- | ----------- |
| POST   | `/events/create`      | ADMIN       |
| GET    | `/events`             | Autenticado |
| GET    | `/events/{id}`        | Autenticado |
| GET    | `/events/{id}/bets`   | ADMIN       |
| PUT    | `/events/{id}/finish` | ADMIN       |
| DELETE | `/events/{id}`        | ADMIN       |

### Transactions (`/api/transactions`)

| Método | Rota                               | Acesso                    |
| ------ | ---------------------------------- | ------------------------- |
| POST   | `/transactions/{walletId}/deposit` | ADMIN ou dono da carteira |

### Bets (`/api/bets`)

| Método | Rota    | Acesso      |
| ------ | ------- | ----------- |
| POST   | `/bets` | Autenticado |

> ⚠️ Documentação completa com exemplos de request/response disponível na [collection do Postman](postman\MoaBet.postman_collection)

---

## 🧪 Testes

O projeto conta com uma suíte de **testes unitários** usando JUnit 5 e Mockito, cobrindo todos os Services da aplicação com isolamento total de dependências externas (banco de dados, contexto de segurança).

**Cobertura:**

- `BetService` — criação de aposta, validação de saldo, validação de status do evento
- `EventService` — criação, finalização (com resolução de apostas), busca, listagem e exclusão
- `UserService` — criação de usuário/carteira, busca (com validação de existência de usuário e carteira), listagem, exclusão
- `TransactionService` — depósito e crédito de ganhos
- `AuthService` — login com validação de credenciais

Cada Service tem cenários de **sucesso** e de **erro** (recurso não encontrado, regra de negócio violada), garantindo que efeitos colaterais (salvar no banco, gerar token, etc.) só acontecem quando deveriam.

Rodar os testes:

```bash
mvn test
```

---

## 🛠️ Rodando localmente

Pré-requisitos: **Java 21** e **Maven**.

```bash
# Clone o repositório
git clone https://github.com/paulovpinheiroo/Moabet_API.git
cd Moabet_API

# Rode a aplicação
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`. O banco H2 (em memória) é populado do zero a cada execução.

Um usuário **ADMIN** é criado automaticamente na inicialização, pronto para testes:

```
email: admin@moabet.com
senha: 123456
```

Para testar como um usuário comum, use o endpoint `POST /api/auth/register` para criar sua própria conta.

Console do H2 disponível em `http://localhost:8080/h2-console`.

---

## 🗺️ Roadmap

- [x] CRUD completo de usuários, eventos, apostas e transações
- [x] Autenticação JWT + BCrypt
- [x] Autorização baseada em Roles (ADMIN/USER)
- [x] Bean Validation com tratamento de erros centralizado
- [x] Migração de `Double` para `BigDecimal` em valores monetários
- [x] Prevenção de IDOR em endpoints sensíveis
- [x] Testes unitários (JUnit + Mockito)
- [ ] Migração para MySQL/PostgreSQL com Docker
- [ ] CI/CD com GitHub Actions
- [ ] Verificação de email no registro

---

## 👨‍💻 Autor

Desenvolvido por **Paulão** ([@paulovpinheiroo](https://github.com/paulovpinheiroo)) como projeto de estudo em Spring Boot, durante o curso de Análise e Desenvolvimento de Sistemas.
