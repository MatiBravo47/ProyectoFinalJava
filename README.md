# 🏪 Sistema de Gestión de Ventas de Sanitarios

<div align="center">

![Java](https://img.shields.io/badge/Java-17+-orange?style=for-the-badge&logo=java)
![SQLite](https://img.shields.io/badge/SQLite-3-blue?style=for-the-badge&logo=sqlite)
![Swing](https://img.shields.io/badge/Swing-GUI-green?style=for-the-badge&logo=java)
![Maven](https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apache-maven)

**Sistema completo de gestión de ventas para comercios de sanitarios con interfaz gráfica moderna**

</div>

---

## 📋 Descripción del Proyecto

El **Sistema de Gestión de Ventas de Sanitarios** es una aplicación de escritorio desarrollada en Java que permite gestionar de manera integral un negocio de sanitarios. El sistema incluye funcionalidades completas para el manejo de clientes, productos, inventario y ventas, con una interfaz gráfica intuitiva desarrollada en Swing.

### 🎯 Características Principales

- ✅ **Gestión Completa de Clientes** - CRUD con validaciones estrictas
- ✅ **Control de Inventario** - Manejo de productos y stock
- ✅ **Sistema de Ventas** - Registro y seguimiento de transacciones
- ✅ **Validaciones Robustas** - DNI, teléfonos, emails y datos obligatorios
- ✅ **Base de Datos Local** - SQLite para persistencia de datos
- ✅ **Interfaz Intuitiva** - GUI moderna con Swing
- ✅ **Arquitectura MVC** - Código organizado y mantenible
- ✅ **Sistema de Excepciones** - Manejo profesional de errores

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 17+ | Lenguaje de programación principal |
| **Swing** | Built-in | Interfaz gráfica de usuario |
| **SQLite** | 3.x | Base de datos local |
| **JDBC** | Built-in | Conexión a base de datos |
| **Maven** | 3.6+ | Gestión de dependencias |
| **Git** | Latest | Control de versiones |

### 📦 Patrones de Diseño Implementados

- **MVC (Modelo-Vista-Controlador)** - Separación de responsabilidades
- **DAO (Data Access Object)** - Abstracción de acceso a datos
- **Singleton** - Conexión única a base de datos
- **Factory** - Creación de excepciones personalizadas

---

## 🚀 Instalación y Configuración

### 📋 Prerrequisitos

Asegúrate de tener instalado en tu sistema:

- ☕ **Java 17 o superior** ([Descargar Java](https://www.oracle.com/java/technologies/downloads/))
- 💻 **IDE compatible** (IntelliJ IDEA, Eclipse, NetBeans, VS Code)
- 🔧 **Maven 3.6+** (opcional, para gestión de dependencias)

### 🔍 Verificar Instalación

```bash
# Verificar Java
java -version

# Verificar Maven (opcional)
mvn -version
```

### 📥 Instalación del Proyecto

#### Opción 1: Clonar desde Git
```bash
git clone https://github.com/MatiBravo47/ProyectoFinalJava.git
cd ProyectoFinalJava
```

#### Opción 2: Descargar ZIP
1. Descarga el archivo ZIP del proyecto
2. Extrae en tu directorio de trabajo
3. Abre el proyecto en tu IDE preferido

### 🏃‍♂️ Ejecución

#### Desde IDE
1. Abre el proyecto en tu IDE
2. Navega a `src/main/java/com/sistemaventas/launcher/App.java`
3. Ejecuta la clase `App` como aplicación Java

#### Desde Terminal
```bash
# Compilar el proyecto
javac -cp "lib/*" -d bin src/main/java/com/sistemaventas/**/*.java

# Ejecutar la aplicación
java -cp "bin:lib/*" com.sistemaventas.launcher.App
```

---

## 📁 Estructura del Proyecto

```
ProyectoFinalJava/
├── 📁 src/main/java/com/sistemaventas/
│   ├── 📁 controlador/          # Lógica de control MVC
│   │   ├── ClienteController.java
│   │   ├── ProductoController.java
│   │   └── VentaController.java
│   ├── 📁 dao/                  # Acceso a datos (DAO Pattern)
│   │   ├── ClienteDAO.java
│   │   ├── ProductoDAO.java
│   │   └── VentaDAO.java
│   ├── 📁 excepciones/          # Sistema de excepciones personalizadas
│   │   ├── SistemaVentasException.java
│   │   ├── ClienteException.java
│   │   ├── ProductoException.java
│   │   ├── VentaException.java
│   │   └── ValidacionException.java
│   ├── 📁 launcher/             # Punto de entrada de la aplicación
│   │   └── App.java
│   ├── 📁 modelo/               # Entidades del dominio
│   │   ├── Cliente.java
│   │   ├── Producto.java
│   │   └── Venta.java
│   ├── 📁 util/                 # Utilidades del sistema
│   │   └── ConexionDB.java
│   └── 📁 vista/                # Interfaces gráficas Swing
│       ├── 📁 cliente/
│       │   ├── ClienteForm.java
│       │   └── ClienteView.java
│       ├── 📁 producto/
│       │   ├── ProductoForm.java
│       │   └── ProductoView.java
│       ├── 📁 tables/
│       │   ├── ClienteTableModel.java
│       │   ├── ProductoTableModel.java
│       │   └── VentaTableModel.java
│       ├── 📁 venta/
│       │   ├── VentaForm.java
│       │   └── VentaView.java
│       └── MainView.java
├── 📁 data/                     # Base de datos SQLite
│   └── sistemaventas.db
├── 📁 lib/                      # Librerías externas
├── 📄 pom.xml                   # Configuración Maven
└── 📄 README.md                 # Este archivo
```

---

## 🎮 Funcionalidades del Sistema

### 👥 Gestión de Clientes

- **✅ Registro de Clientes**: Nombre, DNI, teléfono y email obligatorios
- **✅ Validaciones Estrictas**: 
  - DNI: Exactamente 8 dígitos numéricos
  - Teléfono: Exactamente 10 dígitos numéricos
  - Email: Formato válido con @ y dominio
- **✅ CRUD Completo**: Crear, leer, actualizar y eliminar clientes
- **✅ Búsqueda**: Por nombre, DNI o email
- **✅ Prevención de Duplicados**: Control de DNI y email únicos

### 📦 Gestión de Productos

- **✅ Catálogo de Productos**: Nombre, precio y stock
- **✅ Control de Inventario**: Gestión automática de stock
- **✅ Validaciones de Precio**: Valores positivos y rangos válidos
- **✅ Control de Stock**: Prevención de valores negativos
- **✅ CRUD Completo**: Gestión integral de productos

### 💰 Sistema de Ventas

- **✅ Registro de Ventas**: Cliente, producto, cantidad y fecha
- **✅ Cálculo Automático**: Total basado en precio unitario × cantidad
- **✅ Control de Stock**: Verificación de disponibilidad
- **✅ Historial de Ventas**: Seguimiento completo de transacciones
- **✅ Validaciones**: Cantidades positivas y productos disponibles

### 🛡️ Sistema de Validaciones

- **✅ Validación de DNI**: Formato argentino (8 dígitos)
- **✅ Validación de Teléfono**: Formato nacional (10 dígitos)
- **✅ Validación de Email**: Formato estándar RFC
- **✅ Campos Obligatorios**: Todos los campos requeridos
- **✅ Rangos de Valores**: Precios y cantidades válidas

---

## 🎨 Interfaz de Usuario

### 🖥️ Pantallas Principales

1. **Pantalla Principal**: Navegación entre módulos
2. **Gestión de Clientes**: Formularios y tablas de clientes
3. **Gestión de Productos**: Catálogo y control de inventario
4. **Sistema de Ventas**: Registro y consulta de ventas

---

## 🛡️ Sistema de Excepciones

### 📋 Tipos de Excepciones

| Tipo | Código | Descripción |
|------|--------|-------------|
| **ClienteException** | CLI-xxx | Errores específicos de clientes |
| **ProductoException** | PROD-xxx | Errores específicos de productos |
| **VentaException** | VTA-xxx | Errores específicos de ventas |
| **ValidacionException** | VAL-xxx | Errores de validación de datos |
| **BaseDatosException** | DB-xxx | Errores de persistencia |
| **ConfiguracionException** | CFG-xxx | Errores de configuración |
| **InterfazException** | UI-xxx | Errores de interfaz de usuario |

### 🔧 Manejo de Errores

- **✅ Mensajes Duales**: Técnicos para logs, amigables para usuarios
- **✅ Logging Automático**: Registro detallado de errores
- **✅ Focus Automático**: Enfoque en campos problemáticos
- **✅ Códigos Únicos**: Identificación rápida de problemas

---

## 🧪 Casos de Uso Principales

### 📝 Registro de Cliente
1. Usuario ingresa datos del cliente
2. Sistema valida formato de DNI, teléfono y email
3. Sistema verifica que no exista cliente con mismo DNI/email
4. Cliente se guarda en base de datos
5. Sistema muestra confirmación de éxito

### 🛒 Procesar Venta
1. Usuario selecciona cliente existente
2. Usuario selecciona producto del catálogo
3. Usuario ingresa cantidad deseada
4. Sistema verifica disponibilidad de stock
5. Sistema calcula total automáticamente
6. Venta se registra y stock se actualiza

### 📦 Gestión de Inventario
1. Usuario agrega nuevo producto
2. Sistema valida precio y stock inicial
3. Producto se agrega al catálogo
4. Sistema permite actualizar stock
5. Sistema previene valores negativos

---

## 🔧 Configuración Avanzada

### 🗂️ Ubicación de Base de Datos

Por defecto, la base de datos se crea en:
```
Windows: C:\Users\[Usuario]\SistemaVentas\sistemaventas.db
Linux/Mac: /home/[usuario]/SistemaVentas/sistemaventas.db
```

---

## 🐛 Solución de Problemas

### ❌ Problemas Comunes

#### Error de Conexión a Base de Datos
```
Solución: Verificar permisos de escritura en el directorio del usuario
```

#### Error de Validación de DNI
```
Solución: Asegurar que el DNI tenga exactamente 8 dígitos numéricos
```

#### Error de Stock Insuficiente
```
Solución: Verificar disponibilidad antes de procesar la venta
```
---

## 🚀 Próximas Mejoras

### 🔮 Funcionalidades Planificadas

- [ ] **Reportes y Estadísticas**: Gráficos de ventas y análisis
- [ ] **Backup Automático**: Respaldo periódico de datos
- [ ] **Exportación de Datos**: CSV, Excel, PDF
- [ ] **Sistema de Usuarios**: Autenticación y roles
- [ ] **Notificaciones**: Alertas de stock bajo
- [ ] **API REST**: Integración con sistemas externos

---

## 👨‍💻 Autor

**Matías Bravo**
- 🐙 GitHub: [@MatiBravo47](https://github.com/MatiBravo47)
- 📧 Email: [matiasbravoneron@gmail.com]
- 💼 LinkedIn: [linkedin.com/matibravoneron]

**Tomas Llera**
- 🐙 GitHub: [@TomasLlera](https://github.com/TomasLlera)
- 📧 Email: [tomasllera95@gmail.com]
- 💼 LinkedIn: [linkedin.com/tomasllera]

**Alan Barbera**
- 🐙 GitHub: [@alanbarbera](https://github.com/alanbarbera)
- 📧 Email: [alanbarbera04@gmail.com]
- 💼 LinkedIn: [linkedin.com/alanbarbera]

---

</div>