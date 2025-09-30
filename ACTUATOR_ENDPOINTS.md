# Spring Actuator - Endpoints de Healthcheck

Este documento descreve os endpoints do Spring Actuator disponíveis na API de Tênis do Belvedere.

## Endpoints Disponíveis

### 1. Health Check
- **URL**: `GET /actuator/health`
- **Descrição**: Verifica o status de saúde da aplicação
- **Resposta**: Status UP/DOWN com detalhes dos componentes

### 2. Informações da Aplicação
- **URL**: `GET /actuator/info`
- **Descrição**: Informações básicas sobre a aplicação
- **Resposta**: Dados de build, versão, etc.

### 3. Métricas
- **URL**: `GET /actuator/metrics`
- **Descrição**: Lista todas as métricas disponíveis
- **Resposta**: Lista de métricas do sistema

## Configuração

Os endpoints estão configurados no arquivo `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
      show-components: always
  health:
    db:
      enabled: true
    diskspace:
      enabled: true
```

## Segurança

Todos os endpoints do Actuator (`/actuator/**`) estão configurados para acesso público, sem necessidade de autenticação, permitindo o uso em sistemas de monitoramento e healthcheck.

## Exemplo de Uso

```bash
# Verificar status da aplicação
curl http://localhost:8080/actuator/health

# Obter informações da aplicação
curl http://localhost:8080/actuator/info

# Listar métricas disponíveis
curl http://localhost:8080/actuator/metrics
```
