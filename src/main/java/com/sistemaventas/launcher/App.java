package com.sistemaventas.launcher;

import com.sistemaventas.dao.ProductoDAO;
import com.sistemaventas.modelo.Producto;
import com.sistemaventas.util.ConexionDB;
import com.sistemaventas.vista.MainView;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class App {
    
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE VENTAS DE SANITARIOS ===");
        System.out.println("Base de datos: SQLite");
        
        // Mostrar información de ubicación de la BD
        ConexionDB.mostrarInformacionBaseDatos();
        
        // Probar conexión a SQLite
        ConexionDB.probarConexion();
        
        // Verificar estado actual de la base de datos
        ConexionDB.verificarBaseDatos();
        
        // Pruebas CRUD con base de datos
        pruebasCRUD();
        
        // Verificar nuevamente después de las pruebas
        System.out.println("\n=== DESPUÉS DE PRUEBAS CRUD ===");
        ConexionDB.verificarBaseDatos();
        
        // Iniciar la interfaz gráfica
        MainView.main(args);
        
        System.out.println("\n🚀 ¡Sistema funcionando correctamente con SQLite!");
    }
    
    /**
     * Pruebas completas del sistema CRUD con SQLite
     */
    private static void pruebasCRUD() {
        System.out.println("\n--- PRUEBAS CRUD CON SQLITE ---");
        
        ProductoDAO productoDAO = new ProductoDAO();
        
        try {
            // 1. LISTAR productos existentes (datos de prueba)
            System.out.println("\n1. LISTANDO PRODUCTOS EXISTENTES:");
            List<Producto> productos = productoDAO.obtenerTodos();
            productos.forEach(System.out::println);
            
            // 2. CREAR nuevo producto
            System.out.println("\n2. CREANDO NUEVO PRODUCTO:");
            Producto nuevoProducto = new Producto(
                "Mampara de Vidrio Deluxe", 
                new BigDecimal("45500.00"), 
                3
            );
            
            if (productoDAO.guardar(nuevoProducto)) {
                System.out.println("✓ Producto creado: " + nuevoProducto);
            }
            
            // 3. BUSCAR producto específico
            System.out.println("\n3. BUSCANDO PRODUCTO POR ID:");
            Producto encontrado = productoDAO.buscarPorId(1);
            if (encontrado != null) {
                System.out.println("✓ Encontrado: " + encontrado);
                
                // 4. ACTUALIZAR producto
                System.out.println("\n4. ACTUALIZANDO PRODUCTO:");
                encontrado.setStock(encontrado.getStock() - 2); // Simular venta
                encontrado.setPrecio(encontrado.getPrecio().multiply(new BigDecimal("1.05"))); // Aumento 5%
                
                if (productoDAO.actualizar(encontrado)) {
                    System.out.println("✓ Producto actualizado: " + encontrado);
                }
            }
            
            // 5. BUSCAR por nombre
            System.out.println("\n5. BUSCANDO POR NOMBRE:");
            List<Producto> ferrum = productoDAO.buscarPorNombre("Ferrum");
            System.out.println("Productos Ferrum encontrados: " + ferrum.size());
            ferrum.forEach(p -> System.out.println("  - " + p.getNombre()));
            
            // 6. MOSTRAR inventario actualizado
            System.out.println("\n6. INVENTARIO FINAL:");
            productos = productoDAO.obtenerTodos();
            BigDecimal valorTotal = BigDecimal.ZERO;
            
            for (Producto p : productos) {
                System.out.printf("  %s: $%.2f x %d = $%.2f%n", 
                    p.getNombre(), p.getPrecio(), p.getStock(), p.getValorInventario());
                valorTotal = valorTotal.add(p.getValorInventario());
            }
            
            System.out.printf("💰 VALOR TOTAL DEL INVENTARIO: $%.2f%n", valorTotal);
            
        } catch (SQLException e) {
            System.err.println("❌ Error en pruebas CRUD: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}