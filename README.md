# PayMeNow Backend

API REST para administrar cuentas compartidas de plataformas de streaming (Netflix, Disney+, HBO, etc.):
propietarios, cuentas, perfiles/pantallas asociadas y su historial de pagos.

## Stack
- Java 21
- Spring Boot 3.3.4 (Web, Data JPA, Validation)
- MySQL 8 (mysql-connector-j)
- Lombok
- springdoc-openapi (Swagger UI)

## Antes de correr el proyecto

1. **Base de datos**: tu script `PayMeNow2.sql` crea DOS esquemas duplicados: `PAYMENOW` (mayúsculas) y
   `paymenow` (minúsculas). Este backend está configurado para usar **`paymenow`** (minúsculas), que es la
   convención recomendada en MySQL sobre Linux. Ejecuta el script completo (o al menos la sección de
   `paymenow`) contra tu servidor MySQL local antes de levantar la app.

2. **Credenciales**: ya están cargadas en `src/main/resources/application.properties`
   (usuario `root`, clave `1144177959`, host `localhost:3306`). Ajusta si tu entorno cambia.

3. Verifica que tengas **Java 21** y **Maven** instalados:
   ```bash
   java -version
   mvn -version
   ```

## Cómo correr el proyecto

```bash
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

## Documentación interactiva (Swagger)

Una vez levantado el proyecto, entra a:

```
http://localhost:8080/swagger-ui.html
```

Ahí puedes probar todos los endpoints directamente desde el navegador.

## Endpoints disponibles

Todos siguen el mismo patrón CRUD (`GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `DELETE /{id}`):

| Recurso                         | Base path                          |
|----------------------------------|-------------------------------------|
| Tipos de identificación          | `/api/v1/tipos-identificacion`      |
| Personas                         | `/api/v1/personas`                  |
| Plataformas (Netflix, HBO, etc.) | `/api/v1/plataformas`               |
| Cuentas                          | `/api/v1/cuentas`                   |
| Cuentas asociadas (perfiles)     | `/api/v1/cuentas-asociadas`         |
| Estados de pago                  | `/api/v1/estados-pago`              |
| Pagos                            | `/api/v1/pagos`                     |
| Parámetros                       | `/api/v1/parametros`                |

## Decisiones de diseño

- **Arquitectura en capas**: `entity` → `repository` → `service` (+`impl`) → `controller`, separando la
  persistencia de la lógica de negocio y de la exposición HTTP.
- **DTOs con `record`**: cada entidad tiene su propio DTO inmutable con validaciones de Bean Validation
  (`@NotNull`, `@Email`, `@Size`, etc.), evitando exponer las entidades JPA directamente en la API.
- **Manejo global de errores** (`GlobalExceptionHandler`): respuestas consistentes en formato JSON para
  recursos no encontrados (404), reglas de negocio violadas (409), errores de validación (400,
  con el detalle de cada campo) y errores de integridad de datos (por ejemplo, borrar un registro
  referenciado por otra tabla).
- **Reglas de negocio ya incluidas**:
  - No se permite crear una `Persona` con una identificación duplicada.
  - No se permite asociar más perfiles a una `Cuenta` de los que permite `CANTIDAD_CUENTAS` en su
    `Plataforma` (por ejemplo, si Netflix permite 4 pantallas, no se puede crear una quinta cuenta asociada
    activa).
- **`spring.jpa.hibernate.ddl-auto=validate`**: Hibernate valida que las entidades coincidan con las tablas
  reales, pero nunca modifica el esquema. Tú tienes el control total vía el script `.sql`.
- **Transacciones**: los métodos de lectura usan `@Transactional(readOnly = true)` y los de
  escritura usan transacciones normales, evitando bloqueos innecesarios.

## Paquete base

`co.com.wallacesoft.paymenow`

## Pruebas unitarias

Cada `ServiceImpl` tiene su propia clase de test en `src/test/java/co/com/wallacesoft/paymenow/service`,
usando **JUnit 5 + Mockito** (via `spring-boot-starter-test`, que ya trae ambas librerías y AssertJ, así
que no hace falta agregar nada más al `pom.xml`).

Los repositorios se mockean con `@Mock` y el servicio bajo prueba se inyecta con `@InjectMocks`, así las
pruebas corren en memoria sin necesitar la base de datos real.

Cobertura incluida por servicio: listar, buscar por id (caso feliz y `ResourceNotFoundException`), crear,
actualizar y eliminar. Además se prueban las reglas de negocio específicas:

- `PersonaServiceImplTest`: identificación duplicada al crear/actualizar (`BusinessException`).
- `CuentaAsociadaServiceImplTest`: límite máximo de perfiles/pantallas por plataforma, y que los perfiles
  inactivos no cuenten para ese límite.

Para correr las pruebas:

```bash
mvn test
```

## Próximos pasos sugeridos (no incluidos todavía)

- Autenticación/autorización (por ejemplo Spring Security + JWT), ya que hoy todos los endpoints están
  abiertos.
- Paginación y filtros en los `GET` de listados (`Pageable`) cuando el volumen de datos crezca.
- Tests de integración con Testcontainers para MySQL (las pruebas actuales son unitarias, con mocks).
