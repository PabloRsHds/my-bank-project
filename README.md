# My Bank Project

Projeto de uma aplicação bancária web que simula funcionalidades reais de um banco digital com foco em autenticação, regras de negócio e controle administrativo.

## Sobre o projeto

Esse projeto foi criado para praticar a construção de um sistema completo, indo além de um CRUD simples. A ideia foi simular fluxos reais de um banco digital como cadastro de usuários, verificação de e-mail, envio de documentos, controle de acesso e aprovação administrativa.

A aplicação possui áreas separadas para usuários e administradores, cada uma com responsabilidades bem definidas.

## Funcionalidades

### Área do usuário
- Cadastro com verificação de e-mail
- Login
- Envio de documentos para validação
- Envio de reports
- Feedback sobre documentos enviados
- Notificações
- Configurações de perfil
- Gerenciamento de cartão
- Área de pagamentos

### Área administrativa
- Aprovação ou rejeição de documentos enviados pelos usuários
- Bloqueio e desbloqueio de contas
- Visualização de informações e relatórios do sistema
- Controle de acesso baseado em permissões

## Segurança e controle de acesso
- Autenticação com validação de credenciais
- Separação de permissões entre usuário e administrador
- Proteção de rotas sensíveis
- Validação de dados no backend

## Organização do projeto

O backend foi desenvolvido em Java seguindo uma estrutura organizada por camadas, enquanto o frontend utiliza TypeScript, HTML e CSS. O projeto foi pensado para facilitar manutenção e evolução, mantendo responsabilidades bem definidas.

O banco de dados utilizado é o PostgreSQL, modelado para atender às regras de negócio do sistema.

## Tecnologias utilizadas

### Backend
- Java
- Spring Boot
- Spring Security
- JPA/Hibernate
- JWT
- Feign Client

### Frontend
- TypeScript
- Angular
- HTML5
- CSS3
- RxJS

### Banco de dados
- PostgreSQL

### Autenticação
- JWT (JSON Web Tokens)

### Comunicação
- REST API
- SMTP (envio de e-mails)

## Como rodar o projeto localmente

```bash
git clone https://github.com/PabloRsHds/my-bank-project.git
cd my-bank-project
```