# Dockerización del microservicio

Instrucciones rápidas para construir y ejecutar la imagen del microservicio.

Construir la imagen (desde la raíz del repo):

```bash
docker build -t techstore-api:latest ./microservice
```

Ejecutar con Docker:

```bash
docker run --rm -p 8080:8080 techstore-api:latest
```

O usar docker-compose (desde la raíz del repo):

```bash
docker-compose up --build
```

Notas:
- El Dockerfile usa una build multi-stage: compila con Maven y ejecuta con Eclipse Temurin 17 JRE.
- Si su aplicación necesita una base de datos, configure variables de entorno o añada un servicio en `docker-compose.yml`.
