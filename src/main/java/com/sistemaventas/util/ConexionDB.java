//manejar la conexión a la base de datos SQLite

package com.sistemaventas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {
    
    // Nombre del archivo de base de datos
    private static final String DB_NAME = "sistemaventas.db";
    
    // URL de conexión a SQLite
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    
    // Instancia única de la conexión (Singleton)
    private static Connection conexion = null;
    
    private ConexionDB() {
    }
    
    public static Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");//Cargar el driver de SQLite
                
                conexion = DriverManager.getConnection(DB_URL);//Conectar
                
                // Configuraciones recomendadas para SQLite
                conexion.setAutoCommit(true);
                
                System.out.println("✓ Conexión a SQLite establecida: " + DB_NAME);
                
                crearTablasSiNoExisten();
                
            } catch (ClassNotFoundException e) {
                throw new SQLException("Error: Driver SQLite no encontrado", e);
            } catch (SQLException e) {
                throw new SQLException("Error al conectar con SQLite: " + e.getMessage(), e);
            }
        }
        
        return conexion;
    }
    

     //Cierra la conexión a la base de datos

    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("✓ Conexión a SQLite cerrada");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
    
    //Crea las tablas necesarias si no existen
    private static void crearTablasSiNoExisten() throws SQLException {
        try (Statement stmt = conexion.createStatement()) {
            
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
            
            System.out.println("✓ Tablas creadas/verificadas correctamente");
            
            // Insertar datos de prueba si las tablas están vacías
            insertarDatosPrueba();
            
        } catch (SQLException e) {
            throw new SQLException("Error al crear tablas: " + e.getMessage(), e);
        }
    }
    
    //Inserta algunos datos de prueba si las tablas están vacías
    private static void insertarDatosPrueba() throws SQLException {
        try (Statement stmt = conexion.createStatement()) {
            
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
                    INSERT INTO clientes (nombre, telefono, email) VALUES 
                    ('Juan Pérez', '011-4567-8901', 'juan.perez@email.com'),
                    ('María García', '011-2345-6789', 'maria.garcia@email.com'),
                    ('Carlos López', '011-8765-4321', 'carlos.lopez@email.com');
                """;
                
                stmt.execute(insertProductos);
                stmt.execute(insertClientes);
                
                System.out.println("✓ Datos de prueba insertados");
            }
            
        } catch (SQLException e) {
            // No es crítico si falla la inserción de datos de prueba
            System.out.println("⚠ Advertencia: No se pudieron insertar datos de prueba: " + e.getMessage());
        }
    }
    
    //Método para probar la conexión
   
    public static void probarConexion() {
        try {
            Connection conn = getConexion();
            System.out.println("🔗 Prueba de conexión exitosa");
            
            // Probar una consulta simple
            try (Statement stmt = conn.createStatement()) {
                var rs = stmt.executeQuery("SELECT COUNT(*) as total_productos FROM productos");
                if (rs.next()) {
                    System.out.println("📊 Productos en BD: " + rs.getInt("total_productos"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error en prueba de conexión: " + e.getMessage());
        }
    }
}
