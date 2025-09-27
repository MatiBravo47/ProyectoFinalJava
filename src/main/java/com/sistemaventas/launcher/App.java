package com.sistemaventas.launcher;

import com.sistemaventas.util.ConexionDB;
import com.sistemaventas.vista.MainView;


public class App {
    
    public static void main(String[] args) {
        System.out.println("=== SISTEMA DE VENTAS DE SANITARIOS ===");
        System.out.println("Base de datos: SQLite");
        
        // Probar conexión a SQLite
        ConexionDB.probarConexion();
        
        // Iniciar la interfaz gráfica
        MainView.main(args);
    }
    
}