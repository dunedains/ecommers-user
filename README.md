# Users Service

Microservicio de gestión de usuarios del sistema e-commerce.

## Información general

| Campo | Valor |
|-------|-------|
| Puerto | `8082` |
| Base de datos | `db_users` (PostgreSQL) |
| Contexto | `/api/v1/users` |

## Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/v1/users` | Listar todos los usuarios |
| `GET` | `/api/v1/users/{id}` | Obtener usuario por ID |
| `POST` | `/api/v1/users` | Crear usuario |
| `PUT` | `/api/v1/users/{id}` | Actualizar usuario |
| `DELETE` | `/api/v1/users/{id}` | Eliminar usuario |

## Ejemplo de uso

**Crear usuario:**
```bash
curl -X POST http://localhost:8082/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Felipe Zapata",
    "email": "felipe@example.com",
    "address": "Av. Principal 123"
  }'
```

**Respuesta:**
```json
{
  "id": 1,
  "name": "Felipe Zapata",
  "email": "felipe@example.com",
  "address": "Av. Principal 123"
}
```

## Modelo de datos

```sql
CREATE TABLE users (
    id      BIGINT       GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    email   VARCHAR(255) NOT NULL UNIQUE,
    address VARCHAR(500) NOT NULL
);
```

## Dependencias externas

Ninguna. Servicio autónomo.

## Configuración (variables de entorno Docker)

| Variable | Descripción |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | URL de conexión a PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos |

## Tecnologías

- Java 25 · Spring Boot 4.0.6
- Spring Data JPA · Hibernate 7
- Flyway (migraciones)
- PostgreSQL 16
- Lombok · Bean Validation
