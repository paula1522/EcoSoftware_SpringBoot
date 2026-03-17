## Estructura del proyecto  

```  
 EcoSoftwareSpringboot/  
├── 🗂️.idea/                  
├── 🗂️.mvn/                   
│   └── 🗂️wrapper/  
├── 🗂️src/  
│   ├── 🗂️main/  
│   │   ├── 🗂️java/  
│   │   │   └── 🗂️com/  
│   │   │       └── 🗂️EcoSoftware/  
│   │   │           └── 🗂️Scrum6/
│   │   │               ├── 🗂️Config/              
│   │   │               ├── 🗂️Controller/        
│   │   │               │   ├── 🔵 UsuarioController.java  
│   │   │               │   └── 🔵 RolController.java  
│   │   │               ├── 🗂️DTO/               
│   │   │               │   ├── 🔵 UsuarioDTO.java  
│   │   │               │   └── 🔵 UsuarioEditarDTO.java 
│   │   │               │   └── 🔵 RolDTO.java 
│   │   │               ├── 🗂️Entity/            
│   │   │               │   ├── 🔵 UsuarioEntity.java  
│   │   │               │   └── 🔵 RolEntity.java  
│   │   │               ├── 🗂️Exception/         
│   │   │               ├── 🗂️Implement/         
│   │   │               │   ├── 🔵 UsuarioImplement.java  
│   │   │               │   └── 🔵 RolImplement.java  
│   │   │               ├── 🗂️Repository/        
│   │   │               │   ├── 🟢 UsuarioRepository.java  
│   │   │               │   └── 🟢 RolRepository.java  
│   │   │               ├── 🗂️Service/           
│   │   │               │   ├── 🟢 UsuarioService.java  
│   │   │               │   └── 🟢 RolService.java  
│   │   │               └── 🔵 Scrum6Application.java  
│   │   └── 🗂️resources/  
│   │       ├── 🗂️application.properties         
│   │       └── 🗂️static/                        
│   │       └── 🗂️templates/                     
│   └── 🗂️test/  
│       └── 🗂️java/...   
├── 🗂️target/                  
├── 📝pom.xml                  
├── 📝.gitignore               
└── 📝README.md  

```
---
**Controller  -->  Service (interface)  -->  Implement (lógica)  -->  Repository  -->  Entity (BD)
DTO (opcional) circula entre Controller y Service
Exception captura errores en cualquier capa**  

---
📁Controller/   
Propósito: Maneja las peticiones HTTP y decide qué hacer con ellas.    
Qué contiene: Clases Java anotadas con @RestController o @Controller.   
GET POST PUT DELETE

---
📂DTO/ (Data Transfer Objects)  
Propósito: Contener objetos que transportan datos entre capas (por ejemplo, de Controller a Service o de Service a Controller).  
Qué contiene: Clases Java simples, normalmente solo con atributos, getters/setters (@Data) y validaciones  
---
📂Entity/  
Propósito: Representar las tablas de la base de datos mediante JPA/Hibernate.  
Qué contiene: Clases anotadas con @Entity y @Table. Cada clase normalmente corresponde a una tabla.

---
📂Exception/  
Propósito: Centralizar el manejo de errores de la aplicación.  
Qué contiene: Clases que extienden RuntimeException o Exception y clases con @ControllerAdvice para manejar errores globales.  

---
📂Implement/  
Propósito: Contener las implementaciones concretas de las interfaces de servicio.  
Qué contiene: Clases anotadas con @Service que implementan interfaces en Service/.  

---
📂Repository/  
Propósito: Interactuar directamente con la base de datos.   
Qué contiene: Interfaces que extienden JpaRepository o CrudRepository.  

---
📂Service/  
Propósito: Definir contratos de servicios, es decir, qué operaciones de negocio estarán disponibles.  
Implementa lo declarado en Repository  
Qué contiene: Interfaces con métodos como crearUsuario(), actualizarUsuario(), listarUsuarios().  

---
