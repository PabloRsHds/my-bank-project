# My Bank Project

Projeto de uma aplicação bancária web que simula funcionalidades reais de um banco digital com foco em autenticação regras de negócio e controle administrativo

## Sobre o projeto

Esse projeto foi criado para praticar a construção de um sistema completo indo além de um CRUD simples
A ideia foi simular fluxos reais de um banco digital como cadastro de usuários verificação de e mail envio de documentos controle de acesso e aprovação administrativa

A aplicação possui áreas separadas para usuários e administradores cada uma com responsabilidades bem definidas

## Funcionalidades

Área do usuário

- Cadastro com verificação de e mail

- Login

- Envio de documentos para validação
  
- Envio de reports

- Feedback sobre documentos enviados

- Notificações

- Configurações de perfil

- Gerenciamento de cartão

- Área de pagamentos

Área administrativa

- Aprovação ou rejeição de documentos enviados pelos usuários

- Bloqueio e desbloqueio de contas

- Visualização de informações e relatórios do sistema

- Controle de acesso baseado em permissões

## Segurança e controle de acesso

- Autenticação com validação de credenciais

- Separação de permissões entre usuário e administrador

- Proteção de rotas sensíveis

- Validação de dados no backend

# Organização do projeto

O backend foi desenvolvido em Java seguindo uma estrutura organizada por camadas enquanto o frontend utiliza TypeScript HTML e CSS
O projeto foi pensado para facilitar manutenção e evolução mantendo responsabilidades bem definidas

O banco de dados utilizado é o PostgreSQL modelado para atender às regras de negócio do sistema

## Tecnologias utilizadas

- Backend: Java

- Frontend: TypeScript HTML CSS

- Banco de dados: PostgreSQL

- Autenticação: JWT

- Envio de e mail: SMTP

## Como rodar o projeto localmente
git clone https://github.com/PabloRsHds/my-bank-project.git
cd my-bank-project

## Backend
./mvnw spring-boot:run

## Frontend
npm install
npm run dev


É necessário configurar as variáveis de ambiente antes de executar a aplicação

# Aprendizados

Durante o desenvolvimento desse projeto trabalhei principalmente com

- Autenticação e autorização

- Controle de acesso por perfil

- Regras de negócio aplicadas a um domínio real

- Integração entre frontend e backend

- Modelagem de dados com PostgreSQL

- Organização de projeto backend em Java

- Comunicação entre microserviços utilizando Feign lidando com contratos de API e padronização das respostas

## Próximos passos

- Adicionar circuit breaker em alguns serviços
  
- Adicionar um gateway para requisições
  
- Adicionar testes automatizados
  
- Melhorar validações de segurança
  
- Criar logs e auditoria de ações
  
- Fazer deploy da aplicação

# Considerações finais

Esse projeto foi desenvolvido com foco em aprendizado prático e simulação de um sistema real
Ele representa bem minha evolução técnica e minha capacidade de estruturar uma aplicação completa do backend ao frontend
