# ProyectoFinalJava

## Instalación y Configuración

## Prerrequisitos
Asegúrate de tener instalado:

☕ Java 17 o superior
📦 Apache Maven 3.6+
💻 IDE (NetBeans, IntelliJ IDEA, VS Code, etc.)

### Verificar instalación
### bash Verificar Java
```bash 
java -version
```
### Verificar Maven
mvn -version
## Pasos de instalación

### Clonar/Descargar el proyecto

```bash 
   git clone [URL-DEL-REPOSITORIO]
   cd SistemaVentasSanitarios
```   
### O descargar y extraer el ZIP

Abrir en tu IDE favorito

NetBeans: File → Open Project → Seleccionar carpeta

Instalar dependencias

```bash
mvn clean install
```

Ejecutar el proyecto

bash   mvn exec:java -Dexec.mainClass="com.sistemaventas.App"

