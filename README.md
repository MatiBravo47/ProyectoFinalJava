# Proyecto Final Java - Sistema de Gestión de Ventas
## Tecnologias utilizadas
- Java
- JDBC (Java Database Connectivity) para conexión con la base de datos
- SQLite como base de datos local
- Swing para la interfaz gráfica
- Maven para gestión de dependencias
- Patrón de diseño: MVC (Modelo-Vista-Controlador), DAO (Data Access Object)


## Instalación y Configuración

## Prerrequisitos
Asegúrate de tener instalado:

- ☕ Java 17 o superior
- 💻 IDE NetBeans, IntelliJ IDEA, VS Code, etc.)

## Verificar instalación
### bash Verificar Java
```bash 
java -version
```

## Pasos de instalación

### Clonar/Descargar el proyecto

```bash 
   git clone https://github.com/MatiBravo47/ProyectoFinalJava.git
   cd SistemaVentasSanitarios
```   

### O descargar y extraer el ZIP

NetBeans: File → Open Project → Seleccionar carpeta

### Uso
- CRUD de productos, ventas y clientes.
- Manejo de stock y registro de ventas.
- Persistencia de datos mediante SQLite.
- Conexión a la base de datos realizada con JDBC.

### Estructura proyecto 
- modelo: Clases de las entidades (Producto, Cliente, Venta)
- dao: Clases de acceso a la base de datos
- vista: Interfaces gráficas Swing
- controlador: Lógica de control y coordinación MVC
- util: Utilidades como conexión a base de datos (Singleton)





