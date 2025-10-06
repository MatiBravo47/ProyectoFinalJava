package com.sistemaventas.launcher;

import com.sistemaventas.util.ConexionDB;
import com.sistemaventas.vista.MainView;

/**
 * Clase principal de la aplicación Sistema de Ventas de Sanitarios.
 * <p>
 * Esta clase contiene el punto de entrada (main) de la aplicación y se encarga
 * de inicializar todos los componentes necesarios para el funcionamiento
 * del sistema, incluyendo la conexión a la base de datos y la interfaz gráfica.
 * </p>
 * 
 * <p><strong>Proceso de inicialización:</strong></p>
 * <ol>
 *   <li>Mostrar información del sistema</li>
 *   <li>Probar conexión a la base de datos SQLite</li>
 *   <li>Inicializar la interfaz gráfica principal</li>
 * </ol>
 * 
 * <p><strong>Características del sistema:</strong></p>
 * <ul>
 *   <li>Base de datos: SQLite</li>
 *   <li>Interfaz gráfica: Java Swing</li>
 *   <li>Arquitectura: MVC (Modelo-Vista-Controlador)</li>
 *   <li>Patrón de acceso a datos: DAO</li>
 * </ul>
 * 
 * <p><strong>Requisitos del sistema:</strong></p>
 * <ul>
 *   <li>Java 17 o superior</li>
 *   <li>SQLite JDBC Driver</li>
 *   <li>Permisos de escritura en el directorio del usuario</li>
 * </ul>
 * 
 * @author Matías Bravo, Tomás Llera, Alan Barbera
 * @version 1.0
 * @since 1.0
 * @see com.sistemaventas.util.ConexionDB
 * @see com.sistemaventas.vista.MainView
 */
public class App {
    
    /**
     * Punto de entrada principal de la aplicación.
     * <p>
     * Este método inicia la aplicación del Sistema de Ventas de Sanitarios,
     * realizando las siguientes tareas:
     * </p>
     * <ol>
     *   <li>Mostrar información del sistema en consola</li>
     *   <li>Probar la conexión a la base de datos SQLite</li>
     *   <li>Inicializar y mostrar la interfaz gráfica principal</li>
     * </ol>
     * 
     * <p><strong>Flujo de ejecución:</strong></p>
     * <ol>
     *   <li>Se crea la base de datos si no existe</li>
     *   <li>Se crean las tablas necesarias</li>
     *   <li>Se insertan datos de prueba si es la primera ejecución</li>
     *   <li>Se inicia la interfaz gráfica</li>
     * </ol>
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     * @throws RuntimeException si ocurre un error durante la inicialización
     */
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE VENTAS DE SANITARIOS ===");
        System.out.println("Base de datos: SQLite");
        
        // Probar conexión a SQLite
        ConexionDB.probarConexion();
        
        // Iniciar la interfaz gráfica
        MainView.main(args);
    }
    
}