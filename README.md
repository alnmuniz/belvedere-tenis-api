# belvedere-tenis-api
Api para sistema de reserva de quadra de tenis do Condomínio Belvedere Hill

## 🚀 Executando com Docker

### Pré-requisitos
- Docker instalado na máquina
- PostgreSQL rodando (via Docker ou localmente)

### 1. Configuração das Variáveis de Ambiente

Primeiro, copie o arquivo de exemplo e configure as variáveis de ambiente:

```bash
cp .env.example .env
```

Edite o arquivo `.env` com suas configurações:

```bash
# Banco de Dados
SPRING_DATASOURCE_URL=jdbc:postgresql://172.17.0.2:5432/tennis_court
SPRING_DATASOURCE_USERNAME=admin
SPRING_DATASOURCE_PASSWORD=mysecretpassword

# Auth0
AUTH0_AUDIENCE=https://belvedere.tenis.api
AUTH0_ISSUER_URI=https://dev-xvezaz1t34nrsh0d.us.auth0.com/

# SendGrid
SENDGRID_API_KEY=sua_chave_aqui
SENDGRID_FROM_EMAIL=seu-email@dominio.com

# Frontend
APP_FRONTEND_URL=http://localhost:5173
```

### 2. Build da Imagem Docker

```bash
docker build -t belvedere-tenis-api:latest .
```

### 3. Execução do Container

```bash
docker run -d --name belvedere-tenis-api -p 8080:8080 --env-file .env belvedere-tenis-api:latest
```

### 4. Verificação

A aplicação estará disponível em: **http://localhost:8080**

Para verificar os logs:
```bash
docker logs belvedere-tenis-api
```

Para parar o container:
```bash
docker stop belvedere-tenis-api
docker rm belvedere-tenis-api
```

## 🗄️ Configuração do Banco de Dados

Para executar a aplicação, é necessário ter o PostgreSQL rodando. Você pode usar Docker para facilitar a configuração:

```bash
docker run --rm --name pg-condominio -e POSTGRES_PASSWORD=mysecretpassword -e POSTGRES_USER=admin -e POSTGRES_DB=tennis_court -p 5432:5432 -d postgres
```

### O que este comando faz:

- **`--rm`**: Remove automaticamente o container quando ele for parado, evitando acúmulo de containers órfãos
- **`--name pg-condominio`**: Define o nome do container como "pg-condominio"
- **`-e POSTGRES_PASSWORD=mysecretpassword`**: Define a senha do usuário postgres
- **`-e POSTGRES_USER=admin`**: Cria um usuário chamado "admin" (além do usuário padrão "postgres")
- **`-e POSTGRES_DB=tennis_court`**: Cria automaticamente um banco de dados chamado "tennis_court"
- **`-p 5432:5432`**: Mapeia a porta 5432 do container para a porta 5432 da máquina host
- **`-d`**: Executa o container em modo detached (em background)
- **`postgres`**: Usa a imagem oficial do PostgreSQL

Após executar este comando, o banco de dados estará disponível em `localhost:5432` e a aplicação poderá se conectar usando as credenciais configuradas no `application.yml`.

## 🔧 Desenvolvimento Local

### Executando sem Docker

Para desenvolvimento local, você pode executar a aplicação diretamente:

```bash
# Instalar dependências
./mvnw clean install

# Executar a aplicação
./mvnw spring-boot:run
```

### Configuração para Desenvolvimento

Para desenvolvimento local, ajuste o arquivo `.env`:

```bash
# Para desenvolvimento local, use localhost
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tennis_court
```

## 🐛 Troubleshooting

### Problemas de Conectividade com PostgreSQL

Se a aplicação não conseguir conectar ao banco:

1. **Verifique se o PostgreSQL está rodando:**
   ```bash
   docker ps | grep postgres
   ```

2. **Descubra o IP do container PostgreSQL:**
   ```bash
   docker inspect pg-condominio | grep IPAddress
   ```

3. **Atualize o arquivo `.env` com o IP correto:**
   ```bash
   SPRING_DATASOURCE_URL=jdbc:postgresql://[IP_DO_CONTAINER]:5432/tennis_court
   ```

### Problemas de Build Docker

Se o build falhar:

1. **Verifique se o Java 21 está configurado no Dockerfile**
2. **Limpe o cache do Docker:**
   ```bash
   docker system prune -a
   ```

### Problemas de Permissão

Se houver problemas de permissão no Windows:

1. **Execute o PowerShell como Administrador**
2. **Verifique se o Docker Desktop está rodando**

## Logging de Requisições

A aplicação está configurada para logar automaticamente todas as requisições HTTP recebidas no console (stdout). Cada requisição será registrada com as seguintes informações:

- **Timestamp** da requisição
- **Método HTTP** (GET, POST, PUT, DELETE, etc.)
- **URL completa** (incluindo query parameters)
- **IP do cliente** (formatado em IPv4 quando possível)
- **User-Agent** do navegador/cliente
- **Status da resposta** HTTP
- **Informações de erro** (se houver)

### Exemplo de Log

```
=== REQUISIÇÃO RECEBIDA ===
Timestamp: 2024-01-15 14:30:25
Método: POST
URL: /api/bookings
IP do Cliente: 192.168.1.100
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36
==========================

=== RESPOSTA ENVIADA ===
Timestamp: 2024-01-15 14:30:26
Método: POST
URL: /api/bookings
Status: 201
========================
```

### Tratamento de Endereços IP

O sistema de logging possui tratamento inteligente de endereços IP:

- **IPv4**: Mantém o formato original (ex: `192.168.1.100`)
- **IPv6 localhost**: Converte para IPv4 localhost (`127.0.0.1`)
- **IPv6 mapeado**: Extrai o IPv4 de endereços como `::ffff:192.168.1.1`
- **IPv6 puro**: Mantém o formato original com indicação `(IPv6)`
- **Headers de proxy**: Suporta `X-Forwarded-For` e `X-Real-IP`

Esta funcionalidade é útil para:
- **Debug** de problemas de API
- **Monitoramento** de uso da aplicação
- **Análise** de padrões de acesso
- **Auditoria** de requisições