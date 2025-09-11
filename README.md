# belvedere-tenis-api
Api para sistema de reserva de quadra de tenis do Condomínio Belvedere Hill

## Configuração do Banco de Dados

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