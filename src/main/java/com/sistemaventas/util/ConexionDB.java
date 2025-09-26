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
            
            System.out.println("Nueva conexión SQLite establecida");
            System.out.println("Archivo de BD: " + DB_PATH);
            
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
                
                // Insertar clientes de prueba
                String insertClientes = """
                    INSERT INTO clientes (nombre, telefono, email) VALUES 
                    ('Juan Pérez', '011-4567-8901', 'juan.perez@email.com'),
                    ('María García', '011-2345-6789', 'maria.garcia@email.com'),
                    ('Carlos López', '011-8765-4321', 'carlos.lopez@email.com');
                """;
                
                stmt.execute(insertProductos);
                stmt.execute(insertClientes);
                
                // Confirmar explícitamente
                conn.commit();
                
                System.out.println("Datos de prueba insertados exitosamente");
            }
            
        } catch (SQLException e) {
            // No es crítico si falla la inserción de datos de prueba
            System.out.println("Advertencia: No se pudieron insertar datos de prueba: " + e.getMessage());
        }
    }
    
    /**
     * Método para probar la conexión y mostrar información de la BD
     */
    public static void probarConexion() {
        mostrarInformacionBaseDatos();
        
        try (Connection conn = getConexion();
             Statement stmt = conn.createStatement()) {
            
            System.out.println(" Prueba de conexión exitosa");
            
            // Mostrar información de las tablas
            var rsProductos = stmt.executeQuery("SELECT COUNT(*) as total FROM productos");
            if (rsProductos.next()) {
                System.out.println("Productos en BD: " + rsProductos.getInt("total"));
            }
            
            var rsClientes = stmt.executeQuery("SELECT COUNT(*) as total FROM clientes");
            if (rsClientes.next()) {
                System.out.println("Clientes en BD: " + rsClientes.getInt("total"));
            }
            
            // Mostrar últimos clientes agregados
            var rsUltimos = stmt.executeQuery(
                "SELECT id_cliente, nombre FROM clientes ORDER BY id_cliente DESC LIMIT 3"
            );
            System.out.println(" Últimos clientes:");
            while (rsUltimos.next()) {
                System.out.println("  - ID: " + rsUltimos.getInt("id_cliente") + 
                                 ", Nombre: " + rsUltimos.getString("nombre"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error en prueba de conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Muestra información detallada sobre la ubicación de la base de datos
     */
    public static void mostrarInformacionBaseDatos() {
        System.out.println("\n=== INFORMACIÓN DE BASE DE DATOS ===");
        System.out.println("Directorio de la BD: " + DB_DIRECTORY);
        System.out.println("Archivo de BD: " + DB_NAME);
        
        File dbFile = new File(DB_PATH);
        if (dbFile.exists()) {
            System.out.println("Creado: " + new java.util.Date(dbFile.lastModified()));
        }
    }
    
    /**
     * Método para verificar manualmente los datos en la base
     */
    public static void verificarBaseDatos() {
        System.out.println("\n=== VERIFICACIÓN DE BASE DE DATOS ===");
        System.out.println("Ubicación: " + DB_PATH);
        
        try (Connection conn = getConexion();
             Statement stmt = conn.createStatement()) {
            
            // Verificar productos
            System.out.println("\n PRODUCTOS:");
            var rsProductos = stmt.executeQuery("SELECT * FROM productos ORDER BY id_producto");
            while (rsProductos.next()) {
                System.out.printf("  ID: %d | %s | $%.2f | Stock: %d%n",
                    rsProductos.getInt("id_producto"),
                    rsProductos.getString("nombre"),
                    rsProductos.getDouble("precio"),
                    rsProductos.getInt("stock")
                );
            }
            
            // Verificar clientes
            System.out.println("\nCLIENTES:");
            var rsClientes = stmt.executeQuery("SELECT * FROM clientes ORDER BY id_cliente");
            while (rsClientes.next()) {
                System.out.printf("  ID: %d | %s | Tel: %s | Email: %s%n",
                    rsClientes.getInt("id_cliente"),
                    rsClientes.getString("nombre"),
                    rsClientes.getString("telefono"),
                    rsClientes.getString("email")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("Error al verificar base de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
