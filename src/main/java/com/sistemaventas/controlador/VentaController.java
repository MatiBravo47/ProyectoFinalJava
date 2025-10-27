package com.sistemaventas.controlador;

import com.sistemaventas.dao.VentaDAO;
import com.sistemaventas.dao.ProductoDAO;
import com.sistemaventas.dao.ClienteDAO;
import com.sistemaventas.modelo.Venta;
import com.sistemaventas.modelo.Producto;
import com.sistemaventas.modelo.Cliente;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador refactorizado para manejar la lógica de negocio de Venta
 * Ahora trabaja con objetos Cliente y Producto completos
 * 
 * @author Matías Bravo, Tomás Llera, Alan Barbera
 * @version 2.0
 */
public class VentaController {
    
    private VentaDAO ventaDAO;
    private ProductoDAO productoDAO;
    private ClienteDAO clienteDAO;
    
    public VentaController() {
        this.ventaDAO = new VentaDAO();
        this.productoDAO = new ProductoDAO();
        this.clienteDAO = new ClienteDAO();
    }
    
    /**
     * Registra una nueva venta con objetos Cliente y Producto
     * 
     * @param fecha Fecha de la venta
     * @param idCliente ID del cliente
     * @param idProducto ID del producto
     * @param cantidad Cantidad a vender
     * @return true si se guardó exitosamente
     */
    public boolean registrarVenta(LocalDate fecha, int idCliente, int idProducto, int cantidad) {
        try {
            // Validar fecha
            if (fecha == null) {
                mostrarError("La fecha no puede ser nula");
                return false;
            }
            
            if (fecha.isAfter(LocalDate.now())) {
                mostrarError("No se pueden registrar ventas con fecha futura");
                return false;
            }
            
            // Obtener el cliente
            Cliente cliente = clienteDAO.buscarPorId(idCliente);
            if (cliente == null) {
                mostrarError("Cliente no encontrado con ID: " + idCliente);
                return false;
            }
            
            // Obtener el producto
            Producto producto = productoDAO.buscarPorId(idProducto);
            if (producto == null) {
                mostrarError("Producto no encontrado con ID: " + idProducto);
                return false;
            }
            
            // Validar cantidad
            if (cantidad <= 0) {
                mostrarError("La cantidad debe ser mayor a cero");
                return false;
            }
            
            // Verificar stock suficiente
            if (!producto.hayStock(cantidad)) {
                mostrarError(String.format("Stock insuficiente. Disponible: %d, Solicitado: %d",
                    producto.getStock(), cantidad));
                return false;
            }
            
            // Crear la venta con objetos completos
            Venta venta = new Venta(fecha, cliente, producto, cantidad, producto.getPrecio());
            
            // Guardar la venta
            boolean guardada = ventaDAO.guardar(venta);
            
            if (guardada) {
                mostrarMensaje(String.format("Venta registrada exitosamente\nCliente: %s\nProducto: %s\nTotal: $%.2f",
                    cliente.getNombre(), producto.getNombre(), venta.getTotal()));
                return true;
            } else {
                mostrarError("Error al guardar la venta");
                return false;
            }
            
        } catch (SQLException e) {
            mostrarError("Error de base de datos: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
            return false;
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Sobrecarga del método registrarVenta que acepta una Venta ya construida
     * 
     * @param venta Venta a registrar
     * @return true si se guardó exitosamente
     */
    public boolean registrarVenta(Venta venta) {
        try {
            // Validar la venta
            if (!venta.esValida()) {
                mostrarError("La venta no tiene todos los datos requeridos");
                return false;
            }
            
            // Validaciones de negocio
            validarVenta(venta);
            
            // Verificar que el cliente existe
            Cliente clienteVerificado = clienteDAO.buscarPorId(venta.getCliente().getIdCliente());
            if (clienteVerificado == null) {
                mostrarError("El cliente seleccionado no existe");
                return false;
            }
            
            // Verificar que el producto existe y tiene stock suficiente
            Producto productoVerificado = productoDAO.buscarPorId(venta.getProducto().getIdProducto());
            if (productoVerificado == null) {
                mostrarError("El producto seleccionado no existe");
                return false;
            }
            
            if (!productoVerificado.hayStock(venta.getCantidad())) {
                mostrarError(String.format("Stock insuficiente. Disponible: %d, Solicitado: %d",
                    productoVerificado.getStock(), venta.getCantidad()));
                return false;
            }
            
            // Actualizar el producto en la venta con datos actuales
            venta.setProducto(productoVerificado);
            venta.setCliente(clienteVerificado);
            
            // Asegurar que el precio unitario sea el actual del producto
            venta.setPrecioUnitario(productoVerificado.getPrecio());
            venta.recalcularTotal();
            
            // Guardar la venta
            boolean guardada = ventaDAO.guardar(venta);
            
            if (guardada) {
                System.out.println("✓ Venta registrada: " + venta);
                return true;
            } else {
                mostrarError("Error al guardar la venta");
                return false;
            }
            
        } catch (SQLException e) {
            mostrarError("Error de base de datos: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
            return false;
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Actualiza una venta existente
     * 
     * @param venta Venta con los datos actualizados
     * @return true si se actualizó exitosamente
     */
    public boolean actualizarVenta(Venta venta) {
        try {
            // Validar la venta
            if (!venta.esValida()) {
                mostrarError("La venta no tiene todos los datos requeridos");
                return false;
            }
            
            validarVenta(venta);
            
            // Verificar que la venta existe
            Venta ventaExistente = ventaDAO.buscarPorId(venta.getIdVenta());
            if (ventaExistente == null) {
                mostrarError("La venta no existe en la base de datos");
                return false;
            }
            
            // Verificar cliente
            Cliente cliente = clienteDAO.buscarPorId(venta.getCliente().getIdCliente());
            if (cliente == null) {
                mostrarError("El cliente no existe");
                return false;
            }
            
            // Verificar producto y stock
            Producto producto = productoDAO.buscarPorId(venta.getProducto().getIdProducto());
            if (producto == null) {
                mostrarError("El producto no existe");
                return false;
            }
            
            // Calcular stock disponible considerando la venta original
            int stockDisponible = producto.getStock();
            if (ventaExistente.getIdProducto() == venta.getProducto().getIdProducto()) {
                // Mismo producto: sumar el stock de la venta original
                stockDisponible += ventaExistente.getCantidad();
            }
            
            if (stockDisponible < venta.getCantidad()) {
                mostrarError(String.format("Stock insuficiente. Disponible: %d, Solicitado: %d",
                    stockDisponible, venta.getCantidad()));
                return false;
            }
            
            // Actualizar objetos completos en la venta
            venta.setCliente(cliente);
            venta.setProducto(producto);
            venta.recalcularTotal();
            
            // Actualizar la venta
            boolean actualizada = ventaDAO.actualizar(venta);
            
            if (actualizada) {
                mostrarMensaje("Venta actualizada exitosamente");
                return true;
            } else {
                mostrarError("Error al actualizar la venta");
                return false;
            }
            
        } catch (SQLException e) {
            mostrarError("Error de base de datos: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
            return false;
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina una venta y restaura el stock
     * 
     * @param id ID de la venta a eliminar
     * @return true si se eliminó exitosamente
     */
    public boolean eliminarVenta(int id) {
        try {
            // Verificar que la venta existe
            Venta venta = ventaDAO.buscarPorId(id);
            if (venta == null) {
                mostrarError("La venta no existe");
                return false;
            }
            
            // Confirmar eliminación
            int opcion = JOptionPane.showConfirmDialog(
                null,
                String.format("¿Está seguro de eliminar esta venta?\n\nCliente: %s\nProducto: %s\nCantidad: %d\nTotal: $%.2f\n\nSe restaurará el stock del producto.",
                    venta.getNombreCliente(), venta.getNombreProducto(), venta.getCantidad(), venta.getTotal()),
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (opcion != JOptionPane.YES_OPTION) {
                return false;
            }
            
            // Eliminar la venta
            boolean eliminada = ventaDAO.eliminar(id);
            
            if (eliminada) {
                mostrarMensaje("Venta eliminada exitosamente\nStock restaurado");
                return true;
            } else {
                mostrarError("Error al eliminar la venta");
                return false;
            }
            
        } catch (SQLException e) {
            mostrarError("Error de base de datos: " + e.getMessage());
            return false;
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Busca una venta por ID
     */
    public Venta buscarVentaPorId(int id) {
        try {
            if (id <= 0) {
                mostrarError("El ID de la venta debe ser mayor a cero");
                return null;
            }
            
            return ventaDAO.buscarPorId(id);
            
        } catch (SQLException e) {
            mostrarError("Error al buscar venta: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene todas las ventas
     */
    public List<Venta> obtenerTodasLasVentas() {
        try {
            return ventaDAO.obtenerTodas();
        } catch (SQLException e) {
            mostrarError("Error al obtener ventas: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Busca ventas por cliente
     */
    public List<Venta> buscarVentasPorCliente(int idCliente) {
        try {
            if (idCliente <= 0) {
                mostrarError("El ID del cliente debe ser mayor a cero");
                return List.of();
            }
            
            return ventaDAO.buscarPorCliente(idCliente);
            
        } catch (SQLException e) {
            mostrarError("Error al buscar ventas por cliente: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Busca ventas por producto
     */
    public List<Venta> buscarVentasPorProducto(int idProducto) {
        try {
            if (idProducto <= 0) {
                mostrarError("El ID del producto debe ser mayor a cero");
                return List.of();
            }
            
            return ventaDAO.buscarPorProducto(idProducto);
            
        } catch (SQLException e) {
            mostrarError("Error al buscar ventas por producto: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Busca ventas por rango de fechas
     */
    public List<Venta> buscarVentasPorFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            if (fechaInicio == null || fechaFin == null) {
                mostrarError("Las fechas no pueden ser nulas");
                return List.of();
            }
            
            if (fechaInicio.isAfter(fechaFin)) {
                mostrarError("La fecha de inicio no puede ser posterior a la fecha de fin");
                return List.of();
            }
            
            return ventaDAO.buscarPorFechas(fechaInicio, fechaFin);
            
        } catch (SQLException e) {
            mostrarError("Error al buscar ventas por fechas: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Calcula el total de ventas en un periodo
     */
    public BigDecimal calcularTotalVentas(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            List<Venta> ventas = buscarVentasPorFechas(fechaInicio, fechaFin);
            
            BigDecimal total = BigDecimal.ZERO;
            for (Venta venta : ventas) {
                total = total.add(venta.getTotal());
            }
            
            return total;
            
        } catch (Exception e) {
            mostrarError("Error al calcular total de ventas: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Verifica si un cliente tiene ventas
     */
    public boolean clienteTieneVentas(int idCliente) {
        try {
            return ventaDAO.clienteTieneVentas(idCliente);
        } catch (SQLException e) {
            mostrarError("Error al verificar ventas del cliente: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verifica si un producto tiene ventas
     */
    public boolean productoTieneVentas(int idProducto) {
        try {
            return ventaDAO.productoTieneVentas(idProducto);
        } catch (SQLException e) {
            mostrarError("Error al verificar ventas del producto: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene resumen de ventas del día
     */
    public String obtenerResumenVentasHoy() {
        try {
            LocalDate hoy = LocalDate.now();
            List<Venta> ventasHoy = ventaDAO.buscarPorFechas(hoy, hoy);
            
            if (ventasHoy.isEmpty()) {
                return "No hay ventas registradas hoy";
            }
            
            BigDecimal totalVentas = ventasHoy.stream()
                    .map(Venta::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            int cantidadTotal = ventasHoy.stream()
                    .mapToInt(Venta::getCantidad)
                    .sum();
            
            return String.format("Ventas de hoy:\n- %d transacciones\n- %d productos vendidos\n- Total: $%.2f", 
                               ventasHoy.size(), cantidadTotal, totalVentas);
                               
        } catch (SQLException e) {
            return "Error al obtener resumen de ventas";
        }
    }
    
    /**
     * Obtiene el cliente más frecuente
     */
    public Cliente obtenerMejorCliente() {
        try {
            List<Venta> todasVentas = ventaDAO.obtenerTodas();
            
            if (todasVentas.isEmpty()) {
                return null;
            }
            
            // Contar ventas por cliente
            java.util.Map<Integer, Long> ventasPorCliente = todasVentas.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    v -> v.getCliente().getIdCliente(),
                    java.util.stream.Collectors.counting()
                ));
            
            // Encontrar el cliente con más ventas
            int idMejorCliente = ventasPorCliente.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(0);
            
            return clienteDAO.buscarPorId(idMejorCliente);
            
        } catch (SQLException e) {
            mostrarError("Error al obtener mejor cliente: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene el producto más vendido
     */
    public Producto obtenerProductoMasVendido() {
        try {
            List<Venta> todasVentas = ventaDAO.obtenerTodas();
            
            if (todasVentas.isEmpty()) {
                return null;
            }
            
            // Sumar cantidades vendidas por producto
            java.util.Map<Integer, Integer> cantidadPorProducto = todasVentas.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    v -> v.getProducto().getIdProducto(),
                    java.util.stream.Collectors.summingInt(Venta::getCantidad)
                ));
            
            // Encontrar el producto más vendido
            int idProductoMasVendido = cantidadPorProducto.entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(0);
            
            return productoDAO.buscarPorId(idProductoMasVendido);
            
        } catch (SQLException e) {
            mostrarError("Error al obtener producto más vendido: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Obtiene productos disponibles para ComboBox
     */
    public String[] obtenerProductosParaCombo() {
        try {
            List<Producto> productos = productoDAO.obtenerTodos();
            String[] items = new String[productos.size() + 1];
            items[0] = "Seleccionar producto...";
            
            for (int i = 0; i < productos.size(); i++) {
                Producto producto = productos.get(i);
                items[i + 1] = producto.getIdProducto() + " - " + producto.getNombre() + 
                             " (Stock: " + producto.getStock() + ") - $" + producto.getPrecio();
            }
            
            return items;
        } catch (SQLException e) {
            mostrarError("Error al obtener productos: " + e.getMessage());
            return new String[]{"Error al cargar productos"};
        }
    }
    
    /**
     * Obtiene clientes para ComboBox
     */
    public String[] obtenerClientesParaCombo() {
        try {
            List<Cliente> clientes = clienteDAO.obtenerTodos();
            String[] items = new String[clientes.size() + 1];
            items[0] = "Seleccionar cliente...";
            
            for (int i = 0; i < clientes.size(); i++) {
                Cliente cliente = clientes.get(i);
                items[i + 1] = cliente.getIdCliente() + " - " + cliente.getNombre();
            }
            
            return items;
        } catch (SQLException e) {
            mostrarError("Error al obtener clientes: " + e.getMessage());
            return new String[]{"Error al cargar clientes"};
        }
    }
    
    /**
     * Extrae ID de producto desde selección de ComboBox
     */
    public int extraerIdProductoDeCombo(String seleccion) {
        if (seleccion == null || seleccion.startsWith("Seleccionar") || seleccion.startsWith("Error")) {
            return 0;
        }
        
        try {
            String[] partes = seleccion.split(" - ");
            return Integer.parseInt(partes[0]);
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Extrae ID de cliente desde selección de ComboBox
     */
    public int extraerIdClienteDeCombo(String seleccion) {
        if (seleccion == null || seleccion.startsWith("Seleccionar") || seleccion.startsWith("Error")) {
            return 0;
        }
        
        try {
            String[] partes = seleccion.split(" - ");
            return Integer.parseInt(partes[0]);
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Obtiene precio actual de un producto
     */
    public BigDecimal obtenerPrecioProducto(int idProducto) {
        try {
            Producto producto = productoDAO.buscarPorId(idProducto);
            return producto != null ? producto.getPrecio() : BigDecimal.ZERO;
        } catch (SQLException e) {
            mostrarError("Error al obtener precio del producto: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Calcula total de venta
     */
    public BigDecimal calcularTotal(String cantidad, String precioUnitario) {
        try {
            int cant = Integer.parseInt(cantidad);
            BigDecimal precio = new BigDecimal(precioUnitario);
            return precio.multiply(BigDecimal.valueOf(cant));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Valida que una venta tenga los datos mínimos requeridos
     */
    private void validarVenta(Venta venta) {
        if (venta == null) {
            throw new IllegalArgumentException("La venta no puede ser nula");
        }
        
        // Validar fecha
        if (venta.getFecha() == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
        
        // No permitir ventas futuras
        if (venta.getFecha().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("No se pueden registrar ventas con fecha futura");
        }
        
        // No permitir ventas muy antiguas (más de 1 año)
        if (venta.getFecha().isBefore(LocalDate.now().minusYears(1))) {
            throw new IllegalArgumentException("No se pueden registrar ventas con más de 1 año de antigüedad");
        }
        
        // Validar cliente
        if (venta.getCliente() == null) {
            throw new IllegalArgumentException("Debe seleccionar un cliente válido");
        }
        
        // Validar producto
        if (venta.getProducto() == null) {
            throw new IllegalArgumentException("Debe seleccionar un producto válido");
        }
        
        // Validar cantidad
        if (venta.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        
        if (venta.getCantidad() > 9999) {
            throw new IllegalArgumentException("La cantidad no puede exceder 9,999 unidades");
        }
        
        // Validar precio unitario
        if (venta.getPrecioUnitario() == null || venta.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor a cero");
        }
    }
    
    // Métodos de utilidad para mostrar mensajes
    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}