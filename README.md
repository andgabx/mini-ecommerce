# README de Execução — Sneaker E-commerce Distribuído

Mini e-commerce de tênis exclusivos construído com microsserviços em Kotlin + Ktor.

## Arquitetura

```
Client (curl / Postman)
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

## Pré-requisitos

- **Docker** 24+ e **Docker Compose** v2
- **Java 21+** (apenas para rodar sem Docker)
- **keytool** (incluso no JDK, necessário para gerar o certificado TLS)

---

## Opção 1 — Rodar com Docker Compose (recomendado)

### 1. Gerar o certificado TLS

```bash
bash certs/generate.sh
```

Isso cria `certs/keystore.jks` com um certificado self-signed válido para todos os containers.

### 2. Subir todos os serviços

```bash
docker-compose up --build
```

Aguarde até ver os logs dos 5 containers iniciados. O gateway ficará disponível em `http://localhost:8080`.

### 3. Derrubar

```bash
docker-compose down
```

---

## Opção 2 — Rodar localmente (sem Docker)

Cada serviço deve ser iniciado em um terminal separado, **na ordem abaixo**.

```bash
# Terminal 1 — Users Service (porta 5001)
cd users-service
./gradlew run

# Terminal 2 — Products Replica (porta 5012)
cd products-service
SERVICE_ROLE=replica PORT=5012 DB_PATH=products-replica.db ./gradlew run

# Terminal 3 — Products Primary (porta 5002)
cd products-service
SERVICE_ROLE=primary PORT=5002 REPLICA_URL=http://localhost:5012 ./gradlew run

# Terminal 4 — Orders Service (porta 5003)
cd orders-service
PRODUCTS_PRIMARY_URL=http://localhost:5002 ./gradlew run

# Terminal 5 — API Gateway (porta 8080)
cd gateway-service
JWT_SECRET=dev_secret ./gradlew run
```

---

## Exemplos de uso (curl)

### Registrar usuário

```bash
curl -s -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"João Silva","email":"joao@test.com","password":"senha123"}'
```

### Login (usuário comum)

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@test.com","password":"senha123"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
```

### Login (admin seed)

```bash
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ecommerce.com","password":"admin123"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
```

### Listar produtos (seed com 7 sneakers)

```bash
curl -s http://localhost:8080/products | python3 -m json.tool
```

### Criar produto (requer admin)

```bash
curl -s -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{"name":"Travis Scott x Air Jordan 1","brand":"Jordan","colorway":"Mocha","price":8500.00,"stock":1}'
```

### Criar pedido

```bash
curl -s -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"productId":1,"quantity":1}'
```

### Listar pedidos do usuário

```bash
curl -s http://localhost:8080/orders/2 \
  -H "Authorization: Bearer $TOKEN"
```

### Verificar heartbeat (health dos serviços)

```bash
# Cada serviço expõe GET /health — o gateway verifica a cada 5s
curl -s http://localhost:8080/health

# Simular falha: derrubar orders-service e observar logs do gateway
docker-compose stop orders-service
# aguardar ~10s, então:
curl -s http://localhost:8080/orders/1 -H "Authorization: Bearer $TOKEN"
# retorna 503 Service Unavailable

# Restaurar:
docker-compose start orders-service
```

---

## Usuário admin padrão

| Campo | Valor |
|-------|-------|
| Email | `admin@ecommerce.com` |
| Senha | `admin123` |

Criado automaticamente no primeiro boot do `users-service` se o banco estiver vazio.

---

## Estrutura do repositório

```
mini-ecommerce/
├── certs/
│   └── generate.sh        ← script para gerar keystore TLS
├── gateway-service/       ← porta 8080
├── users-service/         ← porta 5001
├── products-service/      ← portas 5002 (primary) e 5012 (replica)
├── orders-service/        ← porta 5003
└── docker-compose.yml
```

---

## Bônus implementados

### Dashboard de monitoramento

Acesse no browser com o sistema rodando:

```
http://localhost:8080/dashboard
```

Exibe o status de todos os serviços em tempo real (atualiza a cada 5 segundos), com indicador verde/vermelho, URL e contagem de falhas consecutivas.

O endpoint JSON também está disponível:

```bash
curl http://localhost:8080/status
```

### Docker Compose

Toda a infraestrutura sobe com um único comando (`docker-compose up --build`), incluindo os 5 containers com variáveis de ambiente e volumes configurados.

### TLS entre serviços

Toda a comunicação interna entre os containers usa HTTPS com certificado self-signed gerado via `certs/generate.sh`. O gateway e os serviços compartilham o mesmo `keystore.jks`.
