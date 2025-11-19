# Clinica_Back

Backend para el proyecto final de la materia "Práctica profesional". API REST construida con Spring Boot (Java 21), Maven y MySQL.

## Descripción rápida
- Propósito: Servir la API (autenticación, gestión de pacientes, turnos, etc.) para el frontend Clinica_Front.
- Stack principal: Java 21, Spring Boot, Spring Security (JWT), Spring Data JPA, MySQL, Maven.
- Requisito clave: JDK 21+ y MySQL.

---

## Requisitos
- JDK 21 (o la versión que el proyecto especifique).
- Maven (o usar el wrapper incluido: `./mvnw` / `mvnw.cmd`).
- MySQL.
- IDE recomendado: NetBeans o Visual Studio Code (con extensiones de Java / Spring Boot).

---

## Instalación y ejecución (local)

Se recomienda mantener ambos proyectos en una carpeta contenedora, por ejemplo:

```
ClinicaCoc
|
|- Clinica_Back
|- Clinica_Front
```

1) Clonar el repo
```bash
git clone https://github.com/gonzapaglione/Clinica_Back.git
cd Clinica_Back
```

2) Configurar la conexión a MySQL

Revisá `src/main/resources/application.properties` (o `application.yml`) y ajustá los valores. Ejemplo:
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
```

Nota: con `createDatabaseIfNotExist=true` y `spring.jpa.hibernate.ddl-auto=update/create`, no es necesario crear manualmente la base de datos en entornos de desarrollo. Aun así, MySQL debe estar en ejecución y las credenciales deben ser correctas.

3) Ejecutar el backend desde un IDE
- NetBeans:
  - Importar el proyecto (abrir el `pom.xml`).
  - En el árbol de proyectos, ubicarse sobre la clase principal `ClinicaCocApplication` (src/main/java/…).
  - Clic derecho sobre la clase y seleccionar "Run File".
- Visual Studio Code:
  - Abrir la carpeta del proyecto.
  - Abrir `ClinicaCocApplication`.
  - Usar el botón Run sobre la clase `ClinicaCocApplication`.

4) Proseguir con el frontend (desde Visual Studio Code)
- Leer la documentación del frontend en el repositorio: https://github.com/gonzapaglione/Clinica_Front
---

Por defecto Vite sirve el frontend en: `http://localhost:5173` y el backend en `http://localhost:8080`.

---

## Puntos importantes
- El backend expone endpoints en `/api` (ver controladores con `@RequestMapping`).
- En desarrollo habilitá CORS para `http://localhost:5173` o configurá Spring Security/CORS adecuadamente para que el frontend pueda consumir la API.
- Revisá los logs de la aplicación para confirmar que la conexión a la base de datos fue exitosa y que las entidades se migraron/crearon correctamente.

---

## Errores comunes (Backend) — y cómo solucionarlos

- MySQL no arranca / servicio detenido
  - En Windows: verificar en services.msc que el servicio `MySQL` o `mysqld` esté iniciado.
  - En Linux: `systemctl status mysql` / `systemctl start mysql`.

- Conexión rechazada / Communications link failure
  - Asegurate que MySQL esté corriendo y que el `spring.datasource.url` apunte al puerto correcto y host adecuado.

- Access denied for user 'xxx'@'localhost' (using password: YES)
  - Credenciales incorrectas en `application.properties`. Corregir `spring.datasource.username` / `spring.datasource.password`.

- Base de datos/Tablas no creadas
  - Verificar `spring.jpa.hibernate.ddl-auto`. Para desarrollo usar `update`.
  - Confirmar que la URL incluye `createDatabaseIfNotExist=true` si querés que la DB se cree automáticamente.

- Puerto en uso (8080)
  - Cambiar `server.port` en `application.properties` o detener la aplicación que ocupa el puerto.

---
