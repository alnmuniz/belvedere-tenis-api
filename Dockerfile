# --- ESTÁGIO DE BUILD ---
# Usamos uma imagem oficial do Maven com Java 21 para compilar o projeto.
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o pom.xml e os arquivos do maven wrapper primeiro para aproveitar o cache do Docker.
# O Docker só irá baixar as dependências novamente se o pom.xml mudar.
COPY pom.xml .
COPY .mvn/ .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline

# Copia o resto do código fonte
COPY src ./src

# Executa o comando do Maven para empacotar a aplicação, pulando os testes.
# Isso criará o arquivo .jar na pasta /app/target/
RUN ./mvnw package -DskipTests


# --- ESTÁGIO DE EXECUÇÃO ---
# Usamos uma imagem Java 21 "slim", que é bem menor e otimizada para execução.
FROM eclipse-temurin:21-jre-alpine

# Define o diretório de trabalho
WORKDIR /app

# Copia APENAS o arquivo .jar gerado no estágio 'build' para dentro da nossa imagem final.
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta em que a aplicação Spring Boot roda
EXPOSE 8080

# Comando para iniciar a aplicação quando o container for executado
ENTRYPOINT ["java", "-jar", "app.jar"]