# Insurance System API (Prueba Seguros Bolívar)

Este es el backend del sistema de gestión de clientes y pólizas (Vida, Vehículo, Salud), implementado en **Java 17** y **Spring Boot 3**.

## Solución Planteada

Se desarrolló una API RESTful que permite administrar los Clientes y sus respectivas Pólizas. La arquitectura sigue los principios tradicionales de aplicaciones distribuidas mediante modelo de tres capas (Controladores, Servicios y Repositorios).

* **Gestión Excepciones:** Existe un manejador global de excepciones (`GlobalExceptionHandler`) que provee respuestas estandarizadas y claras (400, 404, 500) según las validaciones del negocio.
* **Reglas de negocio integradas:**
  * **Vida:** Validación estricta que impide más de 1 póliza por cliente cotizante y asegura que no sobrepasen los 2 beneficiarios.
  * **Vehículo:** Se permite registrar 1 a N vehículos.
  * **Salud:** Se implementó una lógica donde la cobertura de salud calcula incrementos de tarifa por cada familiar registrado y guarda su parentesco.

## Modelo de Datos

La persistencia se realiza mediante **PostgreSQL** y **Spring Data JPA**.

### Entidades

**Cliente**:
* `id` (PK, Autogenerado)
* `tipoDocumento`, `numeroDocumento` (Unique)
* `nombres`, `apellidos`, `email`, `telefono`, `fechaNacimiento`

**Poliza**:
* `id` (PK)
* `numeroPoliza` (Unique)
* `fechaInicio`, `fechaFin`
* `tipoPoliza` (ENUM: VIDA, VEHICULO, SALUD)
* `coberturaSalud` (ENUM: SOLO_CLIENTE, CLIENTE_Y_PADRES, CLIENTE_ESPOSA_E_HIJOS)
* `valorAsegurado`, `tarifaBase`
* `cliente_id` (FK a Cliente)

**Beneficiario** (Vida y Salud):
* `id` (PK)
* `nombres`, `apellidos`, `tipoDocumento`, `numeroDocumento`, `parentesco`
* `poliza_id` (FK a Poliza)

**VehiculoAsegurado** (Vehículo):
* `id` (PK)
* `placa`, `marca`, `modelo`, `anio`
* `poliza_id` (FK a Poliza)

## Arquitectura de la Aplicación (Local)

* **Java 17** + **Spring Boot 3**
* **Base de datos:** PostgreSQL localmente.
* **Documentación API:** Swagger (OpenAPI 3).
* **Tests:** JUnit 5 con Mockito.

### Cómo Correr la Aplicación Localmente

1. Crear una base de datos en PostgreSQL llamada `insurance_db`.
2. Actualizar las credenciales de la base de datos dentro del archivo `src/main/resources/application.yml` (por defecto `myuser`/`mypassword`).
3. Ejecutar el proyecto mediante Maven:

   ```bash
   ./mvnw spring-boot:run
   ```

4. Acceder a Swagger UI (Documentación Interactiva):
   [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
5. Para ejecutar pruebas unitarias (con un mínimo de 80% de cobertura en Controller y Service):

   ```bash
   ./mvnw clean test
   ```

## Propuesta de Arquitectura en AWS

Para escalar la aplicación y soportar de forma distribuida a 40 millones de clientes de la aseguradora, esta sería la estructura en la nube (AWS):

1. **Amazon Route 53**: Enrutamiento DNS y geolocalización de las solicitudes.
2. **Amazon API Gateway / ALB (Application Load Balancer)**:
   * Para manejar el balanceo de carga, enrutamiento hacia las instancias/contenedores de microservicios y posible manejo de rate-limiting o validación OIDC (Cognito/OAuth).
3. **AWS Fargate / Amazon ECS (o EKS)**:
   * Contenedorizar la aplicación con Docker y correr el backend en AWS Fargate. Esto permite escalabilidad horizontal elástica ("serverless containers").
4. **Amazon Aurora PostgreSQL**:
   * Reemplazar PostgreSQL estándar por **Aurora Serverless** o un clúster **Aurora Multi-AZ** (escritura maestro, lectura réplicas). Aurora escala automáticamente su almacenamiento y memoria según la demanda y permite auto-replicación continua, ideal para bases de datos de alta fiabilidad.
5. **Amazon ElastiCache (Redis)**:
   * Opcional: para el cacheo de llamadas frecuentes (ej: `Consultar Detalles Póliza`), evitando lecturas constantes a Aurora y reduciendo latencia.
6. **Amazon SQS / SNS** (Opcional):
   * Enviar eventos asíncronos cuando se expide una póliza (ej: Notificación por Email/SMS sobre la expedición de una poliza).
7. **AWS CloudWatch & X-Ray**:
   * Monitoreo global, logging de los endpoints de negocio, y análisis de trazas de latencia en las consultas.
