package com.sistemaventas.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
                    telefono VARCHAR(20),
                    email VARCHAR(100) UNIQUE
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
            
            System.out.println("✅ Tablas verificadas correctamente");
            
            // Insertar datos de prueba solo si las tablas están vacías
            insertarDatosPruebaSiEsNecesario(conn);
            
        } catch (SQLException e) {
            throw new SQLException("Error al crear tablas: " + e.getMessage(), e);
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
                
                System.out.println("Insertando datos de prueba...");
                
                // Insertar productos de prueba
                String insertProductos = """
                    INSERT INTO productos (nombre, precio, stock) VALUES 
                    ('Inodoro Ferrum Andina', 25750.50, 8),
                    ('Lavatorio Ferrum Bari', 18900.00, 12),
                    ('Grifería FV Arizona', 8450.25, 25),
                    ('Ducha Hansgrohe Basic', 12300.75, 15),
                    ('Bidet Roca Meridian', 22450.00, 6);
                """;
                               
                stmt.execute(insertProductos);
                
                // Confirmar explícitamente
                conn.commit();
                
                System.out.println("Datos de prueba insertados exitosamente");
            }
            
        } catch (SQLException e) {
            // No es crítico si falla la inserción de datos de prueba
            System.out.println("Advertencia: No se pudieron insertar datos de prueba: " + e.getMessage());
        }
    }
}
