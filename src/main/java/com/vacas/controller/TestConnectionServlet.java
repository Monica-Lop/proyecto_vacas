package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;

import com.vacas.util.ConexionMySQL;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest; // <-- CAMBIO AQUÍ
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/test-conexion")
public class TestConnectionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Prueba de Conexión</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
        out.println(".success { color: green; padding: 10px; border: 1px solid green; }");
        out.println(".error { color: red; padding: 10px; border: 1px solid red; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>🔌 Prueba de Conexión a Base de Datos</h1>");
        
        // Probar conexión
        boolean conexionExitosa = ConexionMySQL.probarConexion();
        
        if (conexionExitosa) {
            out.println("<div class='success'>");
            out.println("<h2>✅ Conexión EXITOSA</h2>");
            out.println("<p>La conexión a MariaDB/MySQL funciona correctamente.</p>");
            out.println("</div>");
        } else {
            out.println("<div class='error'>");
            out.println("<h2>❌ Error de Conexión</h2>");
            out.println("<p>No se pudo conectar a la base de datos.</p>");
            out.println("<p>Verifica:</p>");
            out.println("<ul>");
            out.println("<li>Que MariaDB esté corriendo</li>");
            out.println("<li>El usuario y contraseña en ConexionMySQL.java</li>");
            out.println("<li>Que la base de datos 'proyecto' exista</li>");
            out.println("</ul>");
            out.println("</div>");
        }
        
        out.println("<hr>");
        out.println("<p><a href='javascript:history.back()'>← Volver</a></p>");
        out.println("</body></html>");
    }
}
