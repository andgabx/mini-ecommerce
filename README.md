# Sneaker E-commerce Distribuído

Mini e-commerce de tênis exclusivos construído sobre uma arquitetura de microsserviços em Kotlin + Ktor.

## O que é

Sistema distribuído composto por quatro serviços independentes que se comunicam via HTTP/REST, com replicação de dados, detecção de falha por heartbeat e autenticação via JWT. Desenvolvido como trabalho prático da disciplina de Sistemas Distribuídos (FCCPD).

## Arquitetura

```
Client (curl / Postman / UI)
        │ HTTP :8080
┌───────▼──────────────┐
│      API Gateway     │  ← valida JWT, roteia, heartbeat, round-robin
└──┬──────────┬──────┬─┘
   │ HTTPS    │ HTTPS│ HTTPS
   ▼          ▼      ▼
:5001       :5002  :5003
users    products  orders
         primary
            │ HTTPS
            ▼
          :5012
       products
        replica
```

## Serviços

| Serviço | Porta | Responsabilidade |
|---|---|---|
| `gateway-service` | 8080 | Ponto de entrada único — roteamento, JWT, heartbeat |
| `users-service` | 5001 | Cadastro, login, autenticação com BCrypt + JWT |
| `products-service` | 5002 / 5012 | Catálogo de sneakers com replicação síncrona |
| `orders-service` | 5003 | Criação e listagem de pedidos |

## Funcionalidades

- **Autenticação JWT** — login gera token com `userId`, `email`, `role` e `exp`; endpoints protegidos validados no Gateway
- **Replicação síncrona** — escritas propagadas do primary para a réplica antes de confirmar ao cliente; leituras em round-robin
- **Heartbeat** — Gateway verifica `GET /health` de cada serviço a cada 5s; retorna `503` após 2 falhas consecutivas e registra recovery em log
- **TLS interno** — comunicação entre containers via HTTPS com certificado self-signed
- **Interface visual** — app React em `http://localhost:8080/ui` com carrinho, pedidos e painel admin
- **Dashboard de monitoramento** — status dos serviços em `http://localhost:8080/dashboard`

## Stack

| Componente | Tecnologia |
|---|---|
| Linguagem | Kotlin 2.x |
| Framework HTTP | Ktor 3.x (Netty) |
| Banco de dados | SQLite via Jetbrains Exposed |
| Autenticação | JWT (HMAC-256) + BCrypt |
| Containers | Docker + Docker Compose |
| TLS | Certificado self-signed (keytool JKS) |

## Repositório

```
mini-ecommerce/
├── gateway-service/
├── users-service/
├── products-service/
├── orders-service/
├── certs/
├── docker-compose.yml
├── README_execucao.md
└── relatorio.pdf
```
