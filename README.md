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