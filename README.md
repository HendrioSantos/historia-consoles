# Backend Consoles API

Uma API REST desenvolvida em Spring Boot para gerenciar jogos, consoles e gerações. O projeto foi construído seguindo as melhores práticas de mercado, com foco em segurança, observabilidade, auditoria e arquitetura limpa.

## Ferramentas
* **Java 21** & **Spring Boot 3.3.12**
* **Spring Security** & **JWT (Auth0)** para autenticação e autorização
* **PostgreSQL** (Banco de dados relacional)
* **Flyway** (Migrações e controle de versão do banco)
* **Hibernate Envers** (Auditoria completa de tabelas com sufixo `_aud`, no singular)
* **Micrometer, Prometheus & Actuator** (Observabilidade com rastreamento de logs via MDC, TraceId e SpanId)
* **Springdoc OpenAPI (Swagger)** (Documentação interativa das rotas)
* **Docker & Docker Compose** (Containerização do ambiente)
* **Lombok** (Produtividade no código)

## Padrões de Projeto e SOLID
* **S de SOLID:** Responsabilidade única.
* **Design Patterns:** Implementação dos padrões **Proxy** com o `@Transactional` e **Builder**.
* **DTOs (Data Transfer Objects):** Camada de transporte de dados isolada das entidades do banco.
* **Exclusão Lógica:** Registros desativados com `logico = true`, e `logico = false` para deletar fisicamente do banco.
* **Otimização de Consultas:** Uso de `@EntityGraph` e `LEFT JOIN FETCH` para evitar o problema de performance N+1 do Spring Data JPA.
* **Tratamento Global de Exceções:** Implementado com `@RestControllerAdvice` para retornos de erro limpos e padronizados.
* **Slugs Automáticos:** Conversão de nomes com caracteres especiais em URLs amigáveis (ex: `Primeira Geração` vira `primeira-geracao`).

## Segurança e Níveis de Acesso
A segurança utiliza criptografia de ponta com **Argon2id** para senhas. Níveis de acesso baseados em Roles/Cargos:
* `ADMIN`: Permissão total no sistema.
* `CARGO_GERACAO`: Permissão total de manipulação, restrita à entidade de Gerações.
* `CARGO_CONSOLE`: Permissão total de manipulação, restrita à entidade de Consoles.
* `CARGO_JOGO`: Permissão total de manipulação, restrita à entidade de Jogos.
* `LEITOR`: Permissão para leitura somente (Rotas GET).

### Exemplos de Login e Registro

#### 1. Criar um Novo Usuário
Envie uma requisição **POST** para `/autenticacao/registro`:

*   **URL:** `http://localhost:8080/autenticacao/registro`
*   **Body (JSON):**
```json
{
    "login": "crie-um-usuario",
    "senha": "sua-senha-segura",
    "role": "ADMIN",
    "ativo": true
}
```
> *Nota: Os valores válidos para o campo `role` são: `ADMIN`, `CARGO_GERACAO`, `CARGO_CONSOLE`, `CARGO_JOGO` ou `LEITOR`.*

#### 2. Efetuar Login (Obter Token JWT)
Envie uma requisição **POST** para `/autenticacao/login` utilizando as credenciais cadastradas:

*   **URL:** `http://localhost:8080/autenticacao/login`
*   **Body (JSON):**
```json
{
    "login": "seu-usuario",
    "senha": "sua-senha-segura"
}
```

*   **Exemplo de Resposta de Sucesso (200 OK):**
```json
{
    "login": "novo_usuario",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "mensagem": "Login realizado com sucesso",
    "role": "ADMIN"
}
```

## Como Testar as Rotas no Postman

O projeto inclui uma coleção do Postman pré-configurada com todas as rotas e scripts automatizados de autenticação. O arquivo `postman_collection-API-Historia-Consoles.json` está localizado na raiz deste repositório.

### Passo a Passo para Importar:
1. Abra o seu **Postman**.
2. No canto superior esquerdo, clique no botão **Import**.
3. Selecione ou arraste o arquivo `postman_collection-API-Historia-Consoles.json` que está na raiz do projeto.

### Automação do Bearer Token:
A coleção possui um script automatizado. Siga a ordem abaixo para testar as permissões:
1. Vá até a pasta **Autenticação** e execute a rota de **Login**.
2. O script salvará o Token JWT gerado em uma variável global automaticamente.
3. Todas as outras rotas (Jogos, Consoles e Gerações) já estão configuradas como `Inherit auth from parent` e vão ler esse token sozinhas. Você não precisará copiar e colar nada na aba Headers!

## Como Executar o Projeto Localmente

### Pré-requisitos
* Java 21, Maven e Docker Desktop instalados.

### Execução:

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/HendrioSantos/historia-consoles
   cd historia-consoles
   ```

2. **Configure as variáveis de ambiente:**
   Copie o arquivo de exemplo e configure suas credenciais (o projeto já vem pré-configurado para o perfil de `dev` por padrão):
   ```bash
   cp .env.example .env
   ```

3. **Suba o banco de dados com Docker:**
   ```bash
   docker-compose up -d
   ```

4. **Execute a aplicação:**
   ```bash
   mvn spring-boot:run
   ```

## Testes Automatizados
O projeto possui cobertura completa de testes (Camada de segurança, rotas, usuários e regras de negócio). Para rodar os testes, utilize:
```bash
mvn test
```

## 📊 Observabilidade e Documentação
* **OpenAPI / Swagger UI:** Disponível em `http://localhost:8080/swagger-ui/index.html` (acesse com a aplicação rodando).
* **Métricas Prometheus:** Disponíveis no endpoint do Actuator exposto para coleta de dados.
