# Base de datos   
## MySql

Nombre: ecosoftware_bdSpBt   
Usuario: root  
contraseña:
Url base: http://localhost:8080/api/personas

## Postgres 
Nombre:  
Usuario:  
contraseña:  
Url base:  
Aun no esta configurada  


**Inserts Rol pruebas/Usuario pruebas**  
```
INSERT INTO roles (nombre, descripcion, tipo, estado) VALUES ('Administrador', 'Rol con permisos completos para gestionar la aplicación', 'Administrador', true);
INSERT INTO roles (nombre, descripcion, tipo, estado) VALUES ('Ciudadano', 'Rol para los usuarios ciudadanos que acceden a los servicios', 'Ciudadano', true);
INSERT INTO roles (nombre, descripcion, tipo, estado) VALUES  ('Empresa', 'Rol para empresas que participan en procesos de reciclaje', 'Empresa', true);  
INSERT INTO roles (nombre, descripcion, tipo, estado) VALUES ('Reciclador', 'Rol para recicladores encargados de las recolecciones', 'Reciclador', true); 

INSERT INTO usuarios (rol_id, nombre, contrasena, correo, cedula, telefono, barrio, localidad, estado, NIT, direccion, zona_de_trabajo, horario, certificaciones,
 imagen_perfil, cantidad_minima)
VALUES (1, 'Juan Pérez','contraseña123','juan.perez@example.com','123456789','3001234567','Barrio Ejemplo','Localidad Ejemplo',true, '1234567890',
'Calle Ficticia 123','Zona Norte','9:00 AM - 6:00 PM','Certificación A, Certificación B', 'imagen.jpg', 10);
```

### Anotaciones importantes  
1. Ingresar usuarios por consola de Mysql muestra las fechas como null 00/00/0000

### Modificaciones estructura
1. Los campos zona de trabajo y certificaciones deberian ser tablas aparte,
esto porque podrian ser tuplas o arrays y contener varios registros. 
Esto viola la 1FN de bd, no es un registro atomico.
2. Se **elimino** el campo **tipo_rol** de la tabla **"usuarios"**, era un dato redundante, nombre en la tabla rol ya almacenaba este dato.


