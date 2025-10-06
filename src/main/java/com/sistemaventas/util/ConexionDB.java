package com.sistemaventas.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utilidad para la gestión de conexiones a la base de datos SQLite.
 * <p>
 * Esta clase implementa el patrón Singleton y proporciona métodos para
 * establecer conexiones con la base de datos SQLite, crear tablas,
 * insertar datos de prueba y manejar migraciones de esquema.
 * </p>
 * 
 * <p><strong>Características principales:</strong></p>
 * <ul>
 *   <li>Patrón Singleton para gestión centralizada</li>
 *   <li>Conexiones automáticas a SQLite</li>
 *   <li>Creación automática de tablas</li>
 *   <li>Migración de esquema de base de datos</li>
 *   <li>Inserción de datos de prueba</li>
 *   <li>Gestión de directorios de base de datos</li>
 * </ul>
 * 
 * <p><strong>Ubicación de la base de datos:</strong></p>
 * <p>
 * La base de datos se crea en el directorio del usuario:
 * <code>~/.SistemaVentas/sistemaventas.db</code>
 * </p>
 * 
 * <p><strong>Tablas gestionadas:</strong></p>
 * <ul>
 *   <li>clientes - Información de clientes</li>
 *   <li>productos - Catálogo de productos</li>
 *   <li>ventas - Registro de transacciones</li>
 * </ul>
 * 
 * @author Matías Bravo, Tomás Llera, Alan Barbera
 * @version 1.0
 * @since 1.0
 * @see java.sql.Connection
 * @see java.sql.DriverManager
 */
public class ConexionDB {
    
    // Nombre del archivo de base de datos
    private static final String DB_NAME = "sistemaventas.db";
    
    // Directorio donde se guardará la base de datos
    private static final String DB_DIRECTORY = System.getProperty("user.home") + File.separator + "SistemaVentas";
    
    // Ruta completa del archivo de base de datos
    private static final String DB_PATH = DB_DIRECTORY + File.separator + DB_NAME;
    
    // URL de conexión a SQLite
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    
    // Constructor privado para patrón Singleton
    private ConexionDB() {
    }
    
    /**
     * Obtiene una nueva conexión a la base de datos SQLite
     * IMPORTANTE: Cada operación debe usar su propia conexión
     */
    public static Connection getConexion() throws SQLException {
        Connection conn = null;
        
        try {
            // Crear el directorio si no existe
            crearDirectorioSiNoExiste();
            
            // Cargar el driver de SQLite
            Class.forName("org.sqlite.JDBC");
            
            // Crear conexión con configuraciones específicas
            conn = DriverManager.getConnection(DB_URL);
            
            // Configuraciones importantes para SQLite
            conn.setAutoCommit(true);  // Auto-commit habilitado
            
            // Configurar SQLite para mejor rendimiento y consistencia
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");          // Activar claves foráneas
                stmt.execute("PRAGMA journal_mode = WAL");         // Write-Ahead Logging
                stmt.execute("PRAGMA synchronous = NORMAL");       // Sincronización normal
                stmt.execute("PRAGMA temp_store = MEMORY");        // Tablas temporales en memoria
            }
            
            // Crear tablas si es la primera conexión
            crearTablasSiNoExisten(conn);
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error: Driver SQLite no encontrado", e);
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    e.addSuppressed(closeEx);
                }
            }
            throw new SQLException("Error al conectar con SQLite: " + e.getMessage(), e);
        }
        
        return conn;
    }
    
    /**
     * Crea el directorio para la base de datos si no existe
     */
    private static void crearDirectorioSiNoExiste() {
        File directory = new File(DB_DIRECTORY);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Directorio creado: " + DB_DIRECTORY);
            } else {
                System.err.println("No se pudo crear el directorio: " + DB_DIRECTORY);
            }
        }
        
        // Mostrar información sobre el archivo de base de datos
        File dbFile = new File(DB_PATH);
        if (dbFile.exists()) {
            System.out.println("Archivo de BD encontrado: " + DB_PATH);
        } else {
            System.out.println("Se creará nuevo archivo de BD: " + DB_PATH);
        }
    }
    
    /**
     * Obtiene la ruta completa del archivo de base de datos
     */
    public static String getRutaBaseDatos() {
        return DB_PATH;
    }
    
    /**
     * Obtiene el directorio donde está la base de datos
     */
    public static String getDirectorioBaseDatos() {
        return DB_DIRECTORY;
    }
    
    /**
     * Verifica si el archivo de base de datos existe
     */
    public static boolean existeBaseDatos() {
        File dbFile = new File(DB_PATH);
        return dbFile.exists() && dbFile.length() > 0;
    }
    
    /**
     * Crea las tablas necesarias si no existen
     */
    private static void crearTablasSiNoExisten(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // Tabla PRODUCTOS
            String sqlProductos = """
                CREATE TABLE IF NOT EXISTS productos (
                    id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(100) NOT NULL UNIQUE,
                    precio DECIMAL(10,2) NOT NULL CHECK(precio > 0),
                    stock INTEGER NOT NULL DEFAULT 0 CHECK(stock >= 0)
                )
            """;
            
            // Tabla CLIENTES
            String sqlClientes = """
                CREATE TABLE IF NOT EXISTS clientes (
                    id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(100) NOT NULL,
                    dni VARCHAR(8) NOT NULL UNIQUE,
                    telefono VARCHAR(10) NOT NULL,
                    email VARCHAR(100) NOT NULL UNIQUE
                )
            """;
            
            // Tabla VENTAS
            String sqlVentas = """
                CREATE TABLE IF NOT EXISTS ventas (
                    id_venta INTEGER PRIMARY KEY AUTOINCREMENT,
                    fecha DATE NOT NULL DEFAULT (DATE('now')),
                    id_cliente INTEGER NOT NULL,
                    id_producto INTEGER NOT NULL,
                    cantidad INTEGER NOT NULL CHECK(cantidad > 0),
                    precio_unitario DECIMAL(10,2) NOT NULL,
                    total DECIMAL(10,2) NOT NULL,
                    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
                    FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
                )
            """;
            
            // Ejecutar creación de tablas
            stmt.execute(sqlProductos);
            stmt.execute(sqlClientes);
            stmt.execute(sqlVentas);
            
            // Migrar estructura de base de datos existente si es necesario
            migrarBaseDatosSiEsNecesario(conn);
            
            // Insertar datos de prueba solo si las tablas están vacías
            insertarDatosPruebaSiEsNecesario(conn);
            
        } catch (SQLException e) {
            throw new SQLException("Error al crear tablas: " + e.getMessage(), e);
        }
    }
    
    /**
     * Migra la estructura de la base de datos si es necesario
     */
    private static void migrarBaseDatosSiEsNecesario(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // Verificar si la columna DNI existe en la tabla clientes
            try {
                stmt.executeQuery("SELECT dni FROM clientes LIMIT 1");
                // Si no hay error, la columna ya existe
                System.out.println("Columna DNI ya existe en la tabla clientes");
            } catch (SQLException e) {
                if (e.getMessage().contains("no such column: dni")) {
                    System.out.println("Migrando tabla clientes: agregando columna DNI...");
                    
                    // Agregar columna DNI
                    stmt.execute("ALTER TABLE clientes ADD COLUMN dni VARCHAR(8)");
                    
                    // Actualizar registros existentes con DNI temporal
                    stmt.execute("UPDATE clientes SET dni = '00000000' WHERE dni IS NULL");
                    
                    // Hacer la columna NOT NULL
                    // SQLite no soporta ALTER COLUMN, así que necesitamos recrear la tabla
                    migrarTablaClientesCompleta(conn);
                    
                    System.out.println("Migración completada: columna DNI agregada");
                }
            }
            
            // Verificar si los campos telefono y email son NOT NULL
            // Esto se maneja en la migración completa de la tabla
            
        } catch (SQLException e) {
            System.out.println("Advertencia: Error durante la migración: " + e.getMessage());
            // No lanzar la excepción para no interrumpir el funcionamiento
        }
    }
    
    /**
     * Migra completamente la tabla clientes para agregar restricciones NOT NULL
     */
    private static void migrarTablaClientesCompleta(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // Crear tabla temporal con la nueva estructura
            String sqlNuevaTabla = """
                CREATE TABLE clientes_nueva (
                    id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre VARCHAR(100) NOT NULL,
                    dni VARCHAR(8) NOT NULL UNIQUE,
                    telefono VARCHAR(10) NOT NULL,
                    email VARCHAR(100) NOT NULL UNIQUE
                )
            """;
            
            stmt.execute(sqlNuevaTabla);
            
            // Copiar datos existentes, asignando valores por defecto donde sea necesario
            String sqlCopiarDatos = """
                INSERT INTO clientes_nueva (id_cliente, nombre, dni, telefono, email)
                SELECT 
                    id_cliente,
                    nombre,
                    COALESCE(dni, '00000000') as dni,
                    COALESCE(telefono, '0000000000') as telefono,
                    COALESCE(email, 'sin@email.com') as email
                FROM clientes
            """;
            
            stmt.execute(sqlCopiarDatos);
            
            // Eliminar tabla antigua
            stmt.execute("DROP TABLE clientes");
            
            // Renombrar tabla nueva
            stmt.execute("ALTER TABLE clientes_nueva RENAME TO clientes");
            
            System.out.println("Tabla clientes migrada completamente");
            
        } catch (SQLException e) {
            throw new SQLException("Error durante la migración completa de la tabla clientes: " + e.getMessage(), e);
        }
    }
    
    /**
     * Inserta algunos datos de prueba si las tablas están vacías
     */
    private static void insertarDatosPruebaSiEsNecesario(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            
            // Verificar si ya hay productos
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM productos");
            if (rs.next() && rs.getInt(1) == 0) {
                
                
                // Insertar productos de prueba
                String insertProductos = """
                    INSERT INTO productos (nombre, precio, stock) VALUES 
                    ('Inodoro Ferrum Andina', 25750.50, 8),
                    ('Lavatorio Ferrum Bari', 18900.00, 12),
                    ('Grifería FV Arizona', 8450.25, 25),
                    ('Ducha Hansgrohe Basic', 12300.75, 15),
                    ('Bidet Roca Meridian', 22450.00, 6);
                """;
                
                // Insertar clientes de prueba
                String insertClientes = """
                    INSERT INTO clientes (nombre, dni, telefono, email) VALUES 
                    ('Juan Pérez', '12345678', '0114567890', 'juan.perez@email.com'),
                    ('María García', '87654321', '0112345678', 'maria.garcia@email.com'),
                    ('Carlos López', '11223344', '0118765432', 'carlos.lopez@email.com');
                """;
                
                stmt.execute(insertProductos);
                stmt.execute(insertClientes);
                
                // Confirmar explícitamente
                conn.commit();
            }
            
        } catch (SQLException e) {
            // No es crítico si falla la inserción de datos de prueba
            System.out.println("Advertencia: No se pudieron insertar datos de prueba: " + e.getMessage());
        }
    }
    
    /**
     * Método para probar la conexión
     */

    public static void probarConexion() {
        try (Connection conn = getConexion()) {
            System.out.println("✓ Base de datos conectada correctamente");
        } catch (SQLException e) {
            System.err.println("✗ Error de conexión: " + e.getMessage());
        }
    }
}
