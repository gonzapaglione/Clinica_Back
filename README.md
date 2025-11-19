# Clinica_Back

Backend para el proyecto final de la materia "Práctica profesional". API REST construida con Spring Boot (Java 21), Maven y MySQL.

Descripción rápida
- Propósito: Servir la API (autenticación, gestión de pacientes, turnos, etc.) para el frontend Clinica_Front.
- Stack principal: Java 21, Spring Boot, Spring Security (JWT), Spring Data JPA, MySQL, Maven.
- Requisito clave: JDK 21+ y MySQL.

Requisitos
- JDK 21 (o la versión que el proyecto especifique).
- Maven (o usar el wrapper incluido: `./mvnw` / `mvnw.cmd`).
- MySQL (la aplicación puede crear la base de datos/tablas automáticamente si está configurada).
- IDE recomendado: NetBeans o Visual Studio Code (con extensiones de Java / Spring Boot).
  - NetBeans: recomendado para integración directa con proyectos Maven/Java.
  - VS Code: también válido; instalar Java Extension Pack.
```markdown
Instalación y ejecución (local)
Se recomienda mantener ambos proyectos en una carpeta contenedora, por ejemplo:

ClinicaCoc
|
|- Clinica_Back
|- Clinica_Front

1) Clonar el repo
```bash
git clone https://github.com/gonzapaglione/Clinica_Back.git
cd Clinica_Back

2) Configurar la conexión a MySQL
Revisá `src/main/resources/application.properties` (o `application.yml`) y ajustá:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/clinica_coc?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&noTimezoneConversionForTimeType=true&connectionTimeZone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=<tu_usuario>
spring.datasource.password=<tu_password>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Puerto (opcional)
server.port=8080

Nota: con `createDatabaseIfNotExist=true` y `spring.jpa.hibernate.ddl-auto=update/create`, no es necesario crear manualmente la base de datos en entornos de desarrollo.

3) Ejecutar el backend desde un IDE
- NetBeans:
  - Importar el proyecto.
  - En el árbol de proyectos, ubicarse sobre la clase principal `ClinicaCocApplication` (src/main/java/…).
  - Hacer clic derecho sobre la clase y seleccionar "Run File".
- Visual Studio Code:
  - Abrir la carpeta del proyecto.
  - Abrir `ClinicaCocApplication`.
  - Usar el botón Run (▶) sobre la clase `ClinicaCocApplication` o usar la paleta de comandos "Run Java".
```
Puntos importantes
- No es necesario crear manualmente la base de datos si `spring.jpa.hibernate.ddl-auto` está configurado en `update` o `create` y la URL incluye `createDatabaseIfNotExist=true`. Aun así, MySQL debe estar en ejecución y las credenciales deben ser correctas.
- El backend expone endpoints en `/api` (ver controladores con `@RequestMapping`).
- En desarrollo habilitá CORS para `http://localhost:5173` o configurá Spring Security/CORS adecuadamente para que el frontend pueda consumir la API.

Errores comunes (Backend) — y cómo solucionarlos
- MySQL no arranca / servicio detenido
  - En Windows: verificar en el Administrador de tareas -> pestaña "Servicios" (o "services.msc") que el servicio `MySQL` o `mysqld` esté iniciado.
- Conexión rechazada / Communications link failure
  - Asegurate que MySQL esté corriendo y que el `spring.datasource.url` apunte al puerto correcto.
- Access denied for user 'xxx'@'localhost' (using password: YES)
  - Credenciales incorrectas en `application.properties`. Corregir `spring.datasource.username` / `spring.datasource.password`.
- Base de datos/Tablas no creadas
  - Verificar `spring.jpa.hibernate.ddl-auto`. Si está en `none`, Hibernate no creará tablas. Para desarrollo usar `update` o `create`.
- Puerto en uso (8080)
  - Cambiar `server.port` en `application.properties` o detener la aplicación que ocupa el puerto.
- Problemas al importar/ejecutar en NetBeans
  - Importar `pom.xml` y esperar la descarga de dependencias.
  - Si falla la importación, ejecutar `./mvnw clean package` desde terminal para ver errores completos.

```

Quick start recomendado para desarrollo local
1. Asegurarse que MySQL está corriendo (ver el administrador de servicios / systemctl).
2. Ajustar `src/main/resources/application.properties` con las credenciales correctas.
3. Ejecutar backend desde un IDE.
4. En otra terminal (La de Visual Studio Code) arrancar el frontend (ver README de Clinica_Front):
```bash
cd ../Clinica_Front

npm run dev
```
