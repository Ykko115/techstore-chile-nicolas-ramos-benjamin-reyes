# TechStore Chile — Microservicio de Gestión de Productos

![badge-java][badge-java]
![badge-spring][badge-spring]
![badge-docker][badge-docker]

**Microservicio RESTful** desarrollado con Java 17 y Spring Boot 4.0.6 para administrar un catálogo de productos de la tienda TechStore Chile. Incluye autenticación JWT, persistencia con PostgreSQL y está completamente containerizado con Docker.

---

## 📋 Descripción del Proyecto

**TechStore Chile** es un microservicio de gestión de productos diseñado como proyecto universitario en el contexto de arquitecturas nativas en nube. Proporciona una API REST segura para:

- 🔐 Autenticación y autorización mediante JWT
- 📦 Gestión completa de productos (CRUD)
- 🗄️ Persistencia de datos en PostgreSQL
- 🐳 Despliegue containerizado con Docker
- 📊 Borrado lógico de registros (soft delete)

---

## 👥 Información Académica

| Campo | Detalle |
|-------|---------|
| **Institución** | DuocUC |
| **Asignatura** | Java: Diseño y Construcción de Soluciones Nativas en Nube (JVY0101) |
| **Autores** | Nicolás Ramos, Benjamín Reyes |
| **Año** | 2026 |

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|-----------|---------|----------|
| **Java** | 17 | Lenguaje base |
| **Spring Boot** | 4.0.6 | Framework web |
| **Spring Security** | 4.0.6 | Autenticación |
| **jjwt** | 0.11.5 | JWT tokens |
| **Spring Data JPA** | 4.0.6 | Acceso a datos |
| **Hibernate** | 6.2.x | ORM |
| **PostgreSQL** | 15 | Base de datos |
| **Maven** | 3.8+ | Build tool |
| **Docker** | Latest | Containerización |
| **Docker Compose** | 1.29+ | Orquestación local |
| **Lombok** | Latest | Boilerplate reduction |
| **AWS SDK v2 (SQS)** | 2.25.x | Publicación asíncrona de eventos de auditoría |
| **Amazon ECS Fargate** | - | Orquestación de contenedores en AWS |
| **Amazon API Gateway** | - | Exposición pública y segura de la API |
| **AWS Lambda** | - | Procesamiento serverless de auditoría |
| **GitHub Actions** | - | CI/CD hacia AWS |

---

## 📦 Requisitos Previos

- **Java Development Kit (JDK)** 17 o superior
- **Maven** 3.8.1 o superior
- **Docker** 20.10+
- **Docker Compose** 1.29+
- **Git** 2.20+

### Verificar instalación

```bash
java -version
mvn --version
docker --version
docker-compose --version
```

---

## 📂 Estructura del Proyecto

```
techstore-chile-nicolas-ramos-benjamin-reyes/
│
├── microservice/
│   ├── src/main/java/cl/techstore/api/
│   │   ├── MicroserviceApplication.java       # Clase principal
│   │   │
│   │   ├── controller/
│   │   │   ├── AuthController.java            # Endpoints de autenticación
│   │   │   └── ProductoController.java        # Endpoints de productos
│   │   │
│   │   ├── service/
│   │   │   ├── ProductoService.java           # Lógica de negocio
│   │   │   └── AuditoriaPublisherService.java # Publica eventos de auditoría en SQS (async)
│   │   │
│   │   ├── repository/
│   │   │   └── ProductoRepository.java        # Acceso a datos JPA
│   │   │
│   │   ├── model/
│   │   │   └── Producto.java                  # Entity (id, nombre, descripción, precio, stock, categoría, activo)
│   │   │
│   │   ├── config/
│   │   │   └── SqsConfig.java                 # Bean SqsClient + ObjectMapper, habilita @Async
│   │   │
│   │   ├── security/
│   │   │   ├── JwtUtil.java                   # Generación y validación de JWT
│   │   │   ├── JwtFilter.java                 # Filtro de JWT
│   │   │   └── SecurityConfig.java            # Configuración de Spring Security
│   │   │
│   │   └── dto/
│   │       ├── LoginRequest.java              # DTO para login
│   │       ├── LoginResponse.java             # DTO para respuesta de login
│   │       └── ProductoDTO.java               # DTO para productos
│   │
│   ├── src/main/resources/
│   │   └── application.properties             # Configuración de la aplicación
│   │
│   ├── src/test/java/...                      # Tests unitarios
│   │
│   ├── Dockerfile                             # Build multi-stage (JRE alpine, usuario no-root)
│   ├── .dockerignore                          # Archivos a ignorar en Docker
│   ├── pom.xml                                # Dependencias Maven
│   └── DOCKER.md                              # Documentación de Docker
│
├── .github/workflows/
│   └── deploy.yml                             # Pipeline CI/CD hacia ECR + ECS Fargate
│
├── docker-compose.yml                         # Configuración de servicios
├── README.md                                  # Este archivo
└── .gitignore

```

---

## 🚀 Inicio Rápido

### Opción A: Docker Compose (Recomendado)

La forma más rápida y sencilla de levantar toda la aplicación:

```bash
# 1. Clonar el repositorio
git clone <url-del-repositorio>
cd techstore-chile-nicolas-ramos-benjamin-reyes

# 2. Levantar los servicios (PostgreSQL + API)
docker-compose up --build

# 3. Esperar a que ambos servicios estén listos (aprox. 30-45 segundos)
# La API estará disponible en http://localhost:8080

# 4. (Opcional) Verificar que los contenedores estén corriendo
docker-compose ps

# 5. Detener los servicios
docker-compose down
```

### Opción B: Ejecución Local con Maven

Para desarrollo local sin Docker:

```bash
# 1. Clonar el repositorio
git clone <url-del-repositorio>
cd techstore-chile-nicolas-ramos-benjamin-reyes/microservice

# 2. Compilar el proyecto
mvn clean package -DskipTests

# 3. Ejecutar la aplicación
java -jar target/techstore-api-1.0.0.jar
```

**Nota:** En esta opción necesitas tener PostgreSQL instalado y configurado localmente.

---

## 📋 Servicios en Docker Compose

| Servicio | Puerto | Usuario | Contraseña | Base de Datos |
|----------|--------|---------|-----------|---------------|
| **PostgreSQL** | 5432 | postgres | postgres | techstore |
| **API REST** | 8080 | - | - | - |

### Configuración de docker-compose.yml

```yaml
version: "3.8"
services:
  techstore-api:
    build:
      context: ./microservice
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    restart: unless-stopped
```

---

## 🔨 Comandos Maven

Ejecuta estos comandos desde el directorio `microservice/`:

| Comando | Descripción |
|---------|------------|
| `mvn clean` | Elimina el directorio target |
| `mvn compile` | Compila el código fuente |
| `mvn test` | Ejecuta las pruebas unitarias |
| `mvn package` | Empaqueta la aplicación en .JAR |
| `mvn clean package -DskipTests` | Genera .JAR sin ejecutar tests |
| `mvn clean install` | Compila, prueba e instala en repositorio local |
| `mvn spring-boot:run` | Ejecuta la aplicación directamente (requiere configuración local) |
| `mvn dependency:tree` | Muestra el árbol de dependencias |

---

## 🔐 Autenticación con JWT

Todos los endpoints excepto `/auth/login` requieren autenticación JWT.

### Credenciales de Prueba

```
Usuario: admin@techstore.cl
Contraseña: Admin1234
```

### 1️⃣ Obtener Token (Login)

**Petición:**

```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "username": "admin@techstore.cl",
  "password": "Admin1234"
}
```

**Respuesta (200 OK):**

```json
{
  "token": "eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTY4NDc1NjAwMCwiZXhwIjoxNjg0NzU5NjAwfQ...",
  "tipo": "Bearer",
  "expiracion": 3600
}
```

### 2️⃣ Usar el Token en Peticiones

Incluye el token en el header `Authorization` de todas las peticiones posteriores:

```http
Authorization: Bearer eyJhbGciOiJIUzM4NCIsInR5cCI6IkpXVCJ9...
```

---

## 📡 Documentación de Endpoints

### 🔓 Autenticación

#### POST `/auth/login`
Obtiene un token JWT para autenticarse.

**Request:**
```json
{
  "username": "admin@techstore.cl",
  "password": "Admin1234"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9...",
  "tipo": "Bearer",
  "expiracion": 3600
}
```

---

### 📦 Productos

#### GET `/api/productos`
Lista todos los productos activos.

**Headers:**
```
Authorization: Bearer <token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Laptop Dell XPS 13",
    "descripcion": "Laptop ultraportátil",
    "precio": 1299.99,
    "stock": 15,
    "categoria": "Computadoras",
    "activo": true
  },
  {
    "id": 2,
    "nombre": "Mouse Logitech MX Master",
    "descripcion": "Mouse inalámbrico premium",
    "precio": 99.99,
    "stock": 45,
    "categoria": "Accesorios",
    "activo": true
  }
]
```

---

#### POST `/api/productos`
Crea un nuevo producto.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request:**
```json
{
  "nombre": "Monitor LG 4K",
  "descripcion": "Monitor ultrawide 4K",
  "precio": 799.99,
  "stock": 20,
  "categoria": "Monitores"
}
```

**Response (201 Created):**
```json
{
  "id": 3,
  "nombre": "Monitor LG 4K",
  "descripcion": "Monitor ultrawide 4K",
  "precio": 799.99,
  "stock": 20,
  "categoria": "Monitores",
  "activo": true
}
```

---

#### PUT `/api/productos/{id}`
Actualiza un producto existente.

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request:**
```bash
http://localhost:8080/api/productos/1
```

**Body:**
```json
{
  "nombre": "Laptop Dell XPS 15",
  "descripcion": "Laptop de alto rendimiento",
  "precio": 1599.99,
  "stock": 10,
  "categoria": "Computadoras"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "nombre": "Laptop Dell XPS 15",
  "descripcion": "Laptop de alto rendimiento",
  "precio": 1599.99,
  "stock": 10,
  "categoria": "Computadoras",
  "activo": true
}
```

---

#### DELETE `/api/productos/{id}`
Elimina lógicamente un producto (marca como inactivo).

**Headers:**
```
Authorization: Bearer <token>
```

**Request:**
```bash
http://localhost:8080/api/productos/1
```

**Response (204 No Content)**
```
(Sin cuerpo de respuesta)
```

---

## 📊 Códigos HTTP Esperados

| Código | Descripción | Contexto |
|--------|-------------|----------|
| **200 OK** | Solicitud exitosa | GET, PUT |
| **201 Created** | Recurso creado | POST /api/productos |
| **204 No Content** | Eliminación exitosa | DELETE /api/productos/{id} |
| **400 Bad Request** | Solicitud inválida | Parámetros faltantes o inválidos |
| **401 Unauthorized** | No autenticado | Token inválido o expirado |
| **403 Forbidden** | No autorizado | Sin permisos para la operación |
| **404 Not Found** | Recurso no existe | ID de producto inexistente |
| **500 Internal Server Error** | Error del servidor | Error en la lógica de negocio |

---

## 🐳 Docker

### Construir la imagen manualmente

```bash
docker build -t techstore-api:latest ./microservice
```

### Ejecutar con Docker (sin Compose)

```bash
docker run --rm -p 8080:8080 techstore-api:latest
```

Para más detalles, consulta [microservice/DOCKER.md](microservice/DOCKER.md).

---

## 📚 Arquitectura

### Flujo de Autenticación
1. Usuario envía credenciales a `/auth/login`
2. `AuthController` valida credenciales
3. `JwtUtil` genera token JWT
4. Cliente recibe token y lo incluye en header `Authorization`
5. `JwtFilter` intercepta y valida el token en cada petición

### Flujo de Gestión de Productos
1. Cliente envía petición a `/api/productos`
2. `ProductoController` recibe la petición
3. `ProductoService` ejecuta la lógica de negocio
4. `ProductoRepository` accede a la base de datos
5. Respuesta se envía al cliente

### Patrón de Borrado Lógico
- Los productos no se eliminan de la base de datos
- Se marca el campo `activo = false`
- Las consultas solo retornan productos donde `activo = true`

---

## 🔧 Configuración

### application.properties

La configuración se define en `microservice/src/main/resources/application.properties`:

```properties
# Puerto de la aplicación
server.port=8080

# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/techstore
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL15Dialect
```

---

## ☁️ Despliegue en AWS Academy (Evaluación 3)

Esta sección documenta la migración del microservicio a una arquitectura nativa en AWS: contenedores en **ECS Fargate**, auditoría asíncrona de inventario vía **SQS + Lambda**, exposición segura con **API Gateway**, y despliegue automatizado con **GitHub Actions**.

### Auditoría asíncrona de inventario

Cada operación de escritura (`POST`, `PUT`, `DELETE`) en `/api/productos` publica, de forma asíncrona (`@Async`), un evento JSON a la cola SQS `techstore-audit-queue`:

```json
{
  "accion": "CREAR / MODIFICAR / ELIMINAR",
  "productoId": 12,
  "nombre": "Monitor LG 4K",
  "usuario": "admin@techstore.cl",
  "fecha": "2026-06-24T14:43:00.123Z"
}
```

El campo `usuario` se obtiene del contexto de seguridad (`SecurityContextHolder`), es decir, del nombre de usuario validado por el `JwtFilter` a partir del token JWT.

#### Variables de entorno

| Variable | Descripción | Valor en AWS Academy |
|----------|-------------|----------------------|
| `AWS_REGION` | Región de AWS donde se crean los recursos | la región de tu Learner Lab (ej. `us-east-1`) |
| `SQS_AUDIT_QUEUE_URL` | URL completa de la cola `techstore-audit-queue` | la entregada por la consola SQS al crear la cola |

Se inyectan en la **Task Definition de ECS** (sección *Environment variables* del contenedor). El microservicio **no** requiere `AWS_ACCESS_KEY_ID`/`AWS_SECRET_ACCESS_KEY` explícitos: el SDK (`DefaultCredentialsProvider`) toma automáticamente las credenciales temporales del rol `LabRole` asociado a la tarea ECS.

Si `SQS_AUDIT_QUEUE_URL` no está configurada (por ejemplo, en ejecución local con `docker-compose`), el microservicio registra una advertencia en el log y omite el envío, sin afectar la respuesta HTTP al cliente.

### Escalabilidad y monitoreo: ECS Fargate vs. entorno local (Actividad 1.4)

| Aspecto | Docker Compose (local) | ECS Fargate (AWS) |
|---|---|---|
| **Réplicas** | Una sola instancia fija; escalar exige `docker-compose up --scale` manual, sin balanceo real entre réplicas. | El **ECS Service** mantiene el *desired count* de tareas y las repone automáticamente si fallan. Con **Service Auto Scaling** (target tracking, ej. CPU > 60%) se agregan o quitan tareas en caliente, sin downtime. |
| **Balanceo de carga** | No existe; un único contenedor recibe todo el tráfico del puerto publicado. | El **Application Load Balancer (ALB)** distribuye el tráfico entre todas las tareas Fargate "healthy" del Target Group, y deja de enviarles tráfico si fallan los health checks. |
| **Reinicio ante fallos** | `restart: unless-stopped` reinicia el contenedor en el mismo host Docker; si el host muere, el servicio cae. | ECS supervisa cada tarea (`RUNNING`/`STOPPED`) y lanza una nueva automáticamente ante una falla, sin depender de un host único (Fargate administra el cómputo subyacente). |
| **Límites de CPU/Memoria** | Limitados solo por los recursos del host/Docker Desktop, sin cuotas explícitas a menos que se configuren manualmente. | Definidos explícitamente en la Task Definition: **0.25 vCPU / 0.5 GB RAM** por tarea, reservados de forma exclusiva por Fargate. |
| **Despliegue de nuevas versiones** | Manual: `docker-compose up --build` detiene y reconstruye el contenedor (con downtime). | *Rolling deployment*: ECS levanta tareas nuevas, espera que pasen el health check del ALB y solo entonces retira las antiguas (sin downtime perceptible). |

### Checklist paso a paso en la consola de AWS

> Todo se realiza dentro del **Learner Lab** de AWS Academy (botón *Start Lab*), usando siempre el rol preconfigurado **`LabRole`** donde se solicite un rol de IAM (no se pueden crear roles propios).

1. **Amazon ECR**
   - Crear repositorio privado `techstore-api`.
   - `aws ecr get-login-password --region <region> | docker login --username AWS --password-stdin <ecr_url>`
   - `docker build -t techstore-api ./microservice && docker tag techstore-api:latest <ecr_url>/techstore-api:latest && docker push <ecr_url>/techstore-api:latest`

2. **Amazon SQS**
   - Crear cola estándar `techstore-audit-queue` y copiar su URL (será `SQS_AUDIT_QUEUE_URL`).

3. **AWS Lambda**
   - Crear función `techstore-audit-logger` (Node.js o Python) con el código de [`techstore-cloud-support/notification-function/`](../techstore-cloud-support/notification-function/).
   - Rol de ejecución: **Use an existing role → LabRole**.
   - Agregar trigger de SQS apuntando a `techstore-audit-queue`.

4. **Amazon ECS (Fargate)**
   - Crear Cluster (tipo Fargate).
   - Crear Task Definition: imagen de ECR, **0.25 vCPU / 0.5 GB RAM**, Task Role = Task Execution Role = `LabRole`, variables de entorno (`AWS_REGION`, `SQS_AUDIT_QUEUE_URL`, datasource de RDS/OCI).
   - Crear Service asociado a un **Application Load Balancer** público (Target Group → puerto 8080).
   - Configurar **Service Auto Scaling** (target tracking de CPU, ej. min 1 / max 3 tareas).

5. **Security Groups**
   - SG de las tareas ECS: permitir entrada **solo** desde el SG del ALB (puerto 8080); bloquear acceso público directo.
   - SG del ALB: permitir entrada pública en el puerto 80/443.

6. **Amazon API Gateway**
   - Crear API HTTP (o REST) con una ruta proxy (`/{proxy+}`) que apunte al DNS del ALB.
   - Probar con **Postman**: header `Authorization: Bearer <token>` contra la URL pública del API Gateway.

7. **GitHub Actions**
   - En el repositorio: *Settings → Secrets and variables → Actions* → crear `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN` con los valores de "AWS Details → AWS CLI" del Learner Lab.
   - Ajustar en [`.github/workflows/deploy.yml`](.github/workflows/deploy.yml) los valores de `ECR_REPOSITORY`, `ECS_CLUSTER` y `ECS_SERVICE` si difieren de los nombres reales usados en tu cuenta.
   - Cada `push` a `main` compila con Maven, reconstruye la imagen, la sube a ECR y fuerza un nuevo despliegue del servicio ECS.
   - **Importante:** las credenciales del Learner Lab expiran cada pocas horas — debes actualizar los 3 secretos cada vez que reinicies el laboratorio.

8. **Validación E2E**
   - Login (`POST /auth/login`) → token JWT.
   - CRUD de productos vía API Gateway con el token.
   - Revisar **CloudWatch Logs** del grupo de log de `techstore-audit-logger` para confirmar el procesamiento del evento SQS.
   - Revisar el estado de las tareas en el Cluster ECS (pestaña *Tasks* / *Service*).

9. **Video demostrativo (Actividad 5)**
   - Grabar 3-8 min mostrando: login + CRUD vía Postman contra el API Gateway, el mensaje llegando a SQS, el log en CloudWatch de la Lambda, las tareas activas en ECS Fargate, y un `push` a `main` completando el pipeline en GitHub Actions.

---

## 🌿 Ramas Git

| Rama | Propósito |
|------|-----------|
| **main** | Código en producción (releases estables) |
| **develop** | Rama de desarrollo (integración de features) |

### Flujo de trabajo recomendado

```bash
# Clonar el repositorio
git clone <url-del-repositorio>

# Cambiar a rama develop
git checkout develop

# Crear rama feature
git checkout -b feature/nueva-funcionalidad

# Hacer cambios y commits
git add .
git commit -m "Descripción del cambio"

# Push a la rama feature
git push origin feature/nueva-funcionalidad

# Crear Pull Request en GitHub
```

---

## 📝 Notas de Desarrollo

- **Java 17**: Proyecto utiliza características de Java 17 (records, pattern matching, etc.)
- **Spring Boot 4.0.6**: Versión más reciente con soporte para Java 17
- **PostgreSQL 15**: Database con soporte para JSON, arrays y tipos avanzados
- **Multi-stage Docker**: Reduce el tamaño de la imagen final
- **JWT**: Tokens con expiración en 1 hora (3600 segundos)

---

## ❓ Troubleshooting

### Puerto 8080 ya está en uso
```bash
# Buscar proceso en el puerto
lsof -i :8080

# Cambiar puerto en application.properties
server.port=8081
```

### Error de conexión a PostgreSQL
```bash
# Verificar que PostgreSQL está corriendo
docker-compose ps

# Ver logs de la base de datos
docker-compose logs postgres

# Si aparece: FATAL: password authentication failed for user "postgres"
# recrear el volumen para aplicar las credenciales actuales
docker-compose down -v
docker-compose up --build
```

### Limpiar Docker
```bash
# Eliminar contenedores detenidos
docker-compose down -v

# Reconstruir imágenes
docker-compose up --build --no-cache
```

---

## 📖 Referencias

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [jjwt - JWT for Java](https://github.com/jwtk/jjwt)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Docker Documentation](https://docs.docker.com/)
- [Maven Documentation](https://maven.apache.org/guides/)

---

## 📄 Licencia

Este proyecto es parte de los trabajos académicos de DuocUC. Todos los derechos reservados.

---

## ✉️ Contacto

Para preguntas o sugerencias sobre el proyecto:

- **Nicolás Ramos**: [correo]
- **Benjamín Reyes**: [correo]
- **Profesor**: [correo]

---

**Última actualización:** 11 de mayo de 2026

[badge-java]: https://img.shields.io/badge/Java-17-orange
[badge-spring]: https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen
[badge-docker]: https://img.shields.io/badge/Docker-latest-blue
