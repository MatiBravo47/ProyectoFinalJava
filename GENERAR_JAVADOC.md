# 📚 Generación de Documentación Javadoc

## 🎯 Descripción

Este documento explica cómo generar la documentación Javadoc completa para el Sistema de Ventas de Sanitarios.

## 🚀 Comandos para Generar Javadoc

### 1. Generar Javadoc HTML (Recomendado)

```bash
# Generar documentación HTML en el directorio target/site/apidocs/
mvn javadoc:javadoc

# Abrir la documentación en el navegador (Windows)
start target/site/apidocs/index.html

# Abrir la documentación en el navegador (Linux/Mac)
open target/site/apidocs/index.html
```

### 2. Generar Javadoc JAR

```bash
# Generar JAR con la documentación
mvn javadoc:jar

# El JAR se creará en: target/SistemaVentasSanitarios-1.0-SNAPSHOT-javadoc.jar
```

### 3. Generar Javadoc y JAR en un solo comando

```bash
# Generar tanto HTML como JAR
mvn javadoc:javadoc javadoc:jar
```

### 4. Limpiar y regenerar todo

```bash
# Limpiar proyecto y regenerar documentación
mvn clean javadoc:javadoc
```

## 📁 Ubicación de Archivos Generados

- **HTML**: `target/site/apidocs/`
- **JAR**: `target/SistemaVentasSanitarios-1.0-SNAPSHOT-javadoc.jar`

## ⚙️ Configuración del Plugin

El plugin de Javadoc está configurado con las siguientes características:

- **Idioma**: Español (Argentina)
- **Codificación**: UTF-8
- **Visibilidad**: Incluye métodos privados
- **Título**: Sistema de Ventas de Sanitarios
- **Autores**: Matías Bravo, Tomás Llera, Alan Barbera
- **Versión**: 1.0

## 🔧 Solución de Problemas

### Error de Codificación
```bash
# Si hay problemas con caracteres especiales
mvn clean javadoc:javadoc -Dfile.encoding=UTF-8
```

### Error de Memoria
```bash
# Aumentar memoria para Javadoc
mvn javadoc:javadoc -Dmaven.javadoc.memory=1024m
```

### Verificar Configuración
```bash
# Ver configuración del plugin
mvn help:describe -Dplugin=org.apache.maven.plugins:maven-javadoc-plugin
```

## 📋 Verificación de la Documentación

Después de generar la documentación, verifica que:

1. ✅ La página principal (`index.html`) se abre correctamente
2. ✅ Todas las clases están documentadas
3. ✅ Los métodos tienen descripciones completas
4. ✅ Los parámetros y valores de retorno están documentados
5. ✅ Las excepciones están listadas
6. ✅ Los enlaces entre clases funcionan

## 🌐 Acceso a la Documentación

Una vez generada, la documentación estará disponible en:

- **Local**: `file:///ruta/al/proyecto/target/site/apidocs/index.html`
- **Servidor web**: Subir el contenido de `target/site/apidocs/` a un servidor web

## 📝 Notas Importantes

- La documentación se genera en español
- Incluye métodos privados para desarrollo interno
- Los tags personalizados están configurados en español
- La documentación se regenera automáticamente en cada build

## 🎉 ¡Listo!

Con estos comandos podrás generar una documentación Javadoc completa y profesional para tu proyecto.
