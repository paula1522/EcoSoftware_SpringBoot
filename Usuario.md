# Documentación de la Lógica de Usuario

Este documento describe las entidades y los métodos implementados en las capas **Entity**, **DTO**, **Repository**, **Service**, **Implementación** y **Controller** para la gestión de usuarios. Además, explica con qué archivos interactúan y cómo.

---

## Diagrama de Interacción

**Cliente (Frontend/Angular/Thymeleaf)**  
**↓**
**Controller (UsuarioController)**  
**↓**
**Service (UsuarioService - interfaz)**  
**↓**
**Implementación (UsuarioServiceImpl)**  
**↓**
**Repository (UsuarioRepository + RolRepository)**  
**↓**
**Entity (UsuarioEntity, RolEntity)**  
**↓**
**Base de Datos (Tablas: usuario, rol)**  

**↑**
**DTO (UsuarioDTO, UsuarioEditarDTO)**  
**↔ Usados entre Controller ↔ Service ↔ Implementación para transportar datos**

### 1.Campos de UsuarioEntity

| Campo              | Tipo de dato        | Descripción |
|--------------------|---------------------|-------------|
| `idUsuario`        | `Long`              | Identificador único (PK, autogenerado). |
| `rol`              | `RolEntity`         | Relación **ManyToOne** con `RolEntity`. |
| `nombre`           | `String`            | Nombre del usuario (entre 2 y 70 caracteres). |
| `contrasena`       | `String`            | Contraseña obligatoria. |
| `correo`           | `String` (Email)    | Correo electrónico único y válido. |
| `cedula`           | `String`            | Cédula única y obligatoria. |
| `telefono`         | `String`            | Teléfono obligatorio. |
| `nit`              | `String` (nullable) | NIT único (opcional, empresas). |
| `direccion`        | `String` (nullable) | Dirección física del usuario. |
| `barrio`           | `String`            | Barrio de residencia. |
| `localidad`        | `String`            | Localidad de residencia. |
| `zona_de_trabajo`  | `String` (nullable) | Zona de trabajo (recicladores/empresas). |
| `horario`          | `String` (nullable) | Horario de trabajo (si aplica). |
| `certificaciones`  | `String` (nullable) | Certificaciones del usuario (si aplica). |
| `imagen_perfil`    | `String` (nullable) | Ruta/URL de la imagen de perfil. |
| `cantidad_minima`  | `Integer` (nullable)| Cantidad mínima de material (empresas/recicladores). |
| `estado`           | `Boolean`           | Estado lógico del usuario (`true` activo, `false` inactivo). |
| `fechaCreacion`    | `LocalDateTime`     | Fecha y hora de creación (automática con `@CreationTimestamp`). |
| `fechaActualizacion` | `LocalDateTime`   | Fecha y hora de última actualización (`@PreUpdate`). |
## 2. UsuarioRepository
Ubicación: `com.EcoSoftware.Scrum6.Repository.UsuarioRepository`  
Extiende de `JpaRepository<UsuarioEntity, Long>` y permite interactuar directamente con la base de datos.

| Método | Descripción | Interacción |
|--------|-------------|-------------|
| `findByRol(RolEntity rol)` | Busca usuarios según el rol asignado. | Usado indirectamente desde `UsuarioServiceImpl`. |
| `findByNitAndEstadoTrue(String nit)` | Busca un usuario activo por NIT exacto. | Llamado en `encontrarPorDocumento()`. |
| `findByCedulaAndEstadoTrue(String cedula)` | Busca un usuario activo por Cédula exacta. | Llamado en `encontrarPorDocumento()`. |
| `findByNitContainingIgnoreCaseAndEstadoTrue(String nit)` | Busca usuarios activos cuyo NIT contenga coincidencias. | Llamado en `encontrarPorDocumento()`. |
| `findByCedulaContainingIgnoreCaseAndEstadoTrue(String cedula)` | Busca usuarios activos con coincidencia parcial de Cédula. | Llamado en `encontrarPorDocumento()`. |
| `findByNombreAndEstadoTrue(String nombre)` | Busca usuario activo por nombre exacto. | Llamado en `encontrarPorNombre()`. |
| `findByNombreContainingIgnoreCaseAndEstadoTrue(String nombre)` | Busca usuarios activos cuyo nombre contenga coincidencias. | Llamado en `encontrarPorNombre()`. |
| `findByCorreoAndEstadoTrue(String correo)` | Busca usuario activo por correo exacto. | Llamado en `encontrarPorCorreo()`. |
| `findByCorreoContainingIgnoreCaseAndEstadoTrue(String correo)` | Busca usuarios activos por coincidencia parcial de correo. | Llamado en `encontrarPorCorreo()`. |
| `eliminacionLogica(Long id)` | Marca un usuario como inactivo (estado = false). | Usado en `UsuarioServiceImpl.eliminacionPorEstado()`. |

---

## 3. UsuarioService (Interfaz)
Ubicación: `com.EcoSoftware.Scrum6.Service.UsuarioService`  
Define la lógica de negocio que debe implementar el servicio de usuarios.

| Método | Descripción | Interacción |
|--------|-------------|-------------|
| `listarUsuarios()` | Retorna todos los usuarios. | Implementado en `UsuarioServiceImpl.listarUsuarios()`. |
| `obtenerUsuarioPorId(Long idUsuario)` | Obtiene un usuario específico por su ID. | Implementado en `UsuarioServiceImpl`. |
| `crearUsuario(UsuarioDTO usuarioDTO)` | Registra un nuevo usuario en la BD. | Implementado en `UsuarioServiceImpl`. |
| `actualizarUsuario(Long id, UsuarioEditarDTO usuarioDTO)` | Actualiza datos de un usuario existente. | Implementado en `UsuarioServiceImpl`. |
| `eliminarPersona(Long idUsuario)` | Elimina físicamente un usuario de la BD. | Implementado en `UsuarioServiceImpl`. |
| `eliminacionPorEstado(Long idUsuario)` | Realiza eliminación lógica (estado = false). | Implementado en `UsuarioServiceImpl`. |
| `encontrarPorDocumento(String documento)` | Busca usuarios por NIT o Cédula (exacto o parcial). | Implementado en `UsuarioServiceImpl`. |
| `encontrarPorNombre(String nombre)` | Busca usuarios por nombre (exacto o parcial). | Implementado en `UsuarioServiceImpl`. |
| `encontrarPorCorreo(String correo)` | Busca usuarios por correo (exacto o parcial). | Implementado en `UsuarioServiceImpl`. |

---

## 4. UsuarioServiceImpl
Ubicación: `com.EcoSoftware.Scrum6.Implement.UsuarioServiceImpl`  
Implementa la lógica definida en `UsuarioService`.

| Método | Descripción | Interacción |
|--------|-------------|-------------|
| `listarUsuarios()` | Convierte entidades a DTO y retorna lista. | Llama a `usuarioRepository.findAll()`. |
| `obtenerUsuarioPorId(Long idUsuario)` | Busca un usuario por ID o lanza excepción si no existe. | Llama a `usuarioRepository.findById()`. |
| `crearUsuario(UsuarioDTO usuarioDTO)` | Convierte DTO a Entity, asigna rol y guarda. | Interactúa con `usuarioRepository` y `rolRepository`. |
| `actualizarUsuario(Long id, UsuarioEditarDTO usuarioDTO)` | Actualiza un usuario existente respetando restricciones (ej. ciudadanos). | Llama a `usuarioRepository.findById()` y `save()`. |
| `eliminarPersona(Long idUsuario)` | Elimina físicamente un usuario. | Llama a `usuarioRepository.delete()`. |
| `eliminacionPorEstado(Long idUsuario)` | Cambia estado del usuario a inactivo. | Llama a `usuarioRepository.eliminacionLogica()`. |
| `encontrarPorNombre(String nombre)` | Busca por coincidencia exacta o parcial. | Llama a `findByNombreAndEstadoTrue` y `findByNombreContainingIgnoreCaseAndEstadoTrue`. |
| `encontrarPorDocumento(String documento)` | Verifica primero por coincidencia exacta (Cédula o NIT), luego parcial. | Usa métodos del `UsuarioRepository`. |
| `encontrarPorCorreo(String correo)` | Busca exacto y luego parcial. | Usa `findByCorreoAndEstadoTrue` y `findByCorreoContainingIgnoreCaseAndEstadoTrue`. |
| `convertirAEntity`, `convertirADTO`, `convertirAEditarUsuarioDTO` | Métodos auxiliares para mapear entre Entity y DTO. | Usan `ModelMapper`. |

---

## 5. UsuarioController
Ubicación: `com.EcoSoftware.Scrum6.Controller.UsuarioController`  
Expone la API REST para gestionar usuarios. Depende de `UsuarioService`.

| Método | Descripción | Interacción |
|--------|-------------|-------------|
| `listarPersonas()` (`GET /api/personas`) | Retorna todos los usuarios. | Llama a `usuarioService.listarUsuarios()`. |
| `obtenerUsuarioPorId(Long id)` (`GET /api/personas/{id}`) | Retorna un usuario por ID. | Llama a `usuarioService.obtenerUsuarioPorId()`. |
| `obtenerCorreo(String correo)` (`GET /api/personas/filtrar-correo`) | Busca usuarios por correo. | Llama a `usuarioService.encontrarPorCorreo()`. |
| `obtenerDocumento(String documento)` (`GET /api/personas/filtrar-documento`) | Busca usuarios por Cédula o NIT. | Llama a `usuarioService.encontrarPorDocumento()`. |
| `obtenerNombre(String nombre)` (`GET /api/personas/filtrar-nombre`) | Busca usuarios por nombre. | Llama a `usuarioService.encontrarPorNombre()`. |
| `insertarUsuario(UsuarioDTO usuario)` (`POST /api/personas`) | Registra un nuevo usuario. | Llama a `usuarioService.crearUsuario()`. |
| `actualizarUsuario(Long id, UsuarioEditarDTO usuarioDTO)` (`PUT /api/personas/{id}`) | Actualiza un usuario existente. | Llama a `usuarioService.actualizarUsuario()`. |
| `eliminacionPorEstado(Long id)` (`PATCH /api/personas/eliminar/{id}`) | Realiza eliminación lógica. | Llama a `usuarioService.eliminacionPorEstado()`. |
| *(opcional)* `eliminarUsuario(Long id)` (`DELETE /api/personas/{id}`) | Elimina físicamente un usuario (comentado). | Llama a `usuarioService.eliminarPersona()`. |

---



