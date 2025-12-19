package com.vacas.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionBD {
    // CAMBIA ESTOS DATOS POR LOS TUYOS
    private static final String URL = "jdbc:mysql://localhost:3306/proyecto_vaqueras";
    private static final String USER = "root"; // tu usuario
    private static final String PASS = ""; // tu password
    
    public static Connection getConnection() {
        try {
            // Cargar driver MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Obtener conexión
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}