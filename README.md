# Atividade - API de Autenticação e Gerenciamento de Usuários

#Integrantes
  - Nathan Schiavon 230179292
  - Lucas Foppa 23213637-2
  - Isaac Arantes 23106516-2
  - Matheus Souza 23371012-2
  - Eduardo Aguiar  23000417-2 
  - Matheus Nunes 23044221-2

## Descrição

API RESTful desenvolvida em **Spring Boot** para cadastro, autenticação (JWT) e gerenciamento de usuários, com controle de acesso por papéis (roles: USER e ADMIN).
Permite registro, login, edição de perfil, listagem e exclusão de usuários, além de proteger endpoints sensíveis via Spring Security.

---

## Funcionalidades

- **Cadastro de Usuários:**
  - Registro de novos usuários (nome, e-mail, senha).
  - Definição de papel (role) no cadastro (USER ou ADMIN).

- **Autenticação JWT:**
  - Login com geração de token JWT.
  - Proteção de endpoints via token JWT.

- **Controle de Acesso por Role:**
  - Endpoints exclusivos para ADMIN e USER.
  - ADMIN pode listar, editar e deletar qualquer usuário.
  - USER pode visualizar e editar seu próprio perfil.

- **Gerenciamento de Usuários:**
  - Visualizar e editar perfil.
  - Listar todos os usuários (ADMIN).
  - Buscar, editar e deletar usuário por ID (ADMIN).

---

## Tecnologias Utilizadas

- Java 17+ (ou 21)
- Spring Boot
- Spring Security
- Spring Data JPA
- H2 Database (padrão, para desenvolvimento)
- JWT (JSON Web Token)
- Maven

---

## Como Executar

1. **Clone o repositório:**
   ```sh
   git clone <url-do-repositorio>
   cd atividade
   ```

2. **Execute a aplicação:**
   ```sh
   ./mvnw spring-boot:run
   ```
   Ou, no Windows:
   ```sh
   mvnw.cmd spring-boot:run
   ```

3. **Acesse o H2 Console (opcional):**
   - URL: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
   - JDBC URL: `jdbc:h2:mem:user_auth_db`
   - Usuário: `sa`
   - Senha: (em branco)

---

## Endpoints

### Autenticação

- **Registrar Usuário**
  - `POST /api/auth/register`
  - Body:
    ```json
    {
      "username": "usuario",
      "email": "usuario@email.com",
      "password": "senha123"
    }
    ```

- **Registrar Admin**
  - `POST /api/auth/register/admin`
  - Body igual ao registro de usuário.

- **Login**
  - `POST /api/auth/login`
  - Body:
    ```json
    {
      "username": "usuario",
      "password": "senha123"
    }
    ```
  - Resposta:
    ```json
    {
      "token": "jwt_token",
      "username": "usuario",
      "role": "ROLE_USER"
    }
    ```

### Usuários

> **Todos os endpoints abaixo exigem o header:**  
> `Authorization: Bearer <token>`

- **Ver perfil:**  
  - `GET /api/users/profile`

- **Editar perfil:**  
  - `PUT /api/users/profile`
  - Body igual ao registro.

- **Listar todos os usuários (ADMIN):**  
  - `GET /api/users`

- **Buscar usuário por ID (ADMIN ou o próprio usuário):**  
  - `GET /api/users/{id}`

- **Editar usuário por ID (ADMIN):**  
  - `PUT /api/users/{id}`

- **Deletar usuário por ID (ADMIN):**  
  - `DELETE /api/users/{id}`

---

## Configurações Importantes

- **Porta do servidor:** `8080`
- **Banco de dados:** H2 em memória (padrão para desenvolvimento)
- **JWT:**
  - Secret e tempo de expiração configurados em `application.properties`
- **Console H2:**
  - Ativado em `/h2-console`

---

## Testando com Postman

1. **Registre um usuário ou admin.**
2. **Faça login para obter o token JWT.**
3. **Inclua o token no header Authorization para acessar endpoints protegidos.**

---

## Observações

- Para uso em produção, configure um banco de dados real (MySQL/PostgreSQL) e altere as propriedades em `application.properties`.
- O acesso ao console H2 é apenas para desenvolvimento.
- O projeto não utiliza Lombok.

---

## Licença

Este projeto é livre para fins acadêmicos e de estudo. 