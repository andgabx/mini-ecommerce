# README de Execução

> **Ambiente:** Linux ou macOS. No Windows, use WSL2.

## Pré-requisitos

- **Docker Desktop** (macOS/Linux) — inclui o `docker compose`
- **Java 21+** com `keytool` (incluso no JDK)

### Instalar Docker Desktop

**macOS:**
```bash
brew install --cask docker
```
Ou baixe em [docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop/).

**Ubuntu/Debian:**
```bash
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER && newgrp docker
```

### Instalar Java 21

**macOS:**
```bash
brew install openjdk@21
brew link openjdk@21
```

**Ubuntu/Debian:**
```bash
sudo apt install openjdk-21-jdk
```

---

## Problemas comuns antes de rodar

### `docker-credential-desktop: executable file not found in $PATH`

O Docker Desktop precisa estar **aberto e rodando** antes de executar qualquer comando `docker`. Se estiver rodando e o erro persistir, o helper de credenciais não está no PATH do shell. Corrija com:

```bash
echo 'export PATH="/Applications/Docker.app/Contents/Resources/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

---

## Opção 1 — Docker Compose (recomendado)

Certifique-se de que o Docker Desktop está aberto antes de prosseguir.

```bash
bash certs/generate.sh
docker compose up --build
```

Gateway disponível em `http://localhost:8080`.

```bash
docker compose down
```

---

## Opção 2 — Local (sem Docker)

Abrir 5 terminais na ordem abaixo:

```bash
# Terminal 1 — Products Replica
cd products-service
SERVICE_ROLE=replica PORT=5012 DB_PATH=products-replica.db ./gradlew run

# Terminal 2 — Products Primary
cd products-service
SERVICE_ROLE=primary PORT=5002 REPLICA_URL=http://localhost:5012 ./gradlew run

# Terminal 3 — Users Service
cd users-service
./gradlew run

# Terminal 4 — Orders Service
cd orders-service
PRODUCTS_PRIMARY_URL=http://localhost:5002 ./gradlew run

# Terminal 5 — API Gateway
cd gateway-service
JWT_SECRET=dev_secret ./gradlew run
```

---

## Exemplos de uso

```bash
# Registrar usuário
curl -s -X POST http://localhost:8080/users/register \
  -H "Content-Type: application/json" \
  -d '{"name":"João Silva","email":"joao@test.com","password":"senha123"}'

# Login usuário
TOKEN=$(curl -s -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"joao@test.com","password":"senha123"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# Login admin
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/users/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ecommerce.com","password":"admin123"}' | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# Listar produtos
curl -s http://localhost:8080/products

# Criar produto (admin)
curl -s -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{"name":"Air Jordan 1","brand":"Jordan","colorway":"Chicago","price":2800.00,"stock":5}'

# Criar pedido
curl -s -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"productId":1,"quantity":1}'

# Listar pedidos
curl -s http://localhost:8080/orders/2 -H "Authorization: Bearer $TOKEN"

# Testar heartbeat
docker compose stop orders-service
sleep 12
curl -s http://localhost:8080/orders/1 -H "Authorization: Bearer $TOKEN"
docker compose start orders-service
```

---

## Admin padrão

| Email | Senha |
|---|---|
| `admin@ecommerce.com` | `admin123` |

---

## Páginas disponíveis

| URL | Descrição |
|---|---|
| `http://localhost:8080/ui` | Interface React completa |
| `http://localhost:8080/dashboard` | Monitor de serviços |
