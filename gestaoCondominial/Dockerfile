# Estágio base com JDK 17 e dependências
FROM eclipse-temurin:17-jdk-jammy AS base
WORKDIR /app

# Copia arquivos do Maven para cache de camadas
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY aplicacao/pom.xml aplicacao/pom.xml
COPY apresentacao-backend/pom.xml apresentacao-backend/pom.xml
COPY apresentacao-frontend/pom.xml apresentacao-frontend/pom.xml
COPY dominio-compartilhado/pom.xml dominio-compartilhado/pom.xml
COPY dominio-dominium/pom.xml dominio-dominium/pom.xml
COPY infraestrutura/pom.xml infraestrutura/pom.xml

# Permissão e download de dependências
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copia o restante do código
COPY . .

# --- Estágio para Rodar o Backend ---
FROM base AS backend
# Instala os módulos ignorando os testes para agilizar o boot
RUN ./mvnw install -DskipTests
EXPOSE 8080
ENTRYPOINT ["./mvnw", "spring-boot:run", "-pl", "apresentacao-backend"]

# --- Estágio para Rodar os Testes ---
FROM base AS test
WORKDIR /app/apresentacao-backend
ENTRYPOINT ["../mvnw", "test", "-Dtest=RunCucumberTest"]
