package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vacas.utils.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/reportes/usuario/*")
public class ReporteUsuarioServlet extends HttpServlet {
    
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // Verificar que sea usuario común
        HttpSession session = request.getSession(false);
        if (session == null || !"COMUN".equals(session.getAttribute("tipoUsuario"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"No autorizado\"}");
            return;
        }
        
        int usuarioId = (int) session.getAttribute("usuarioId");
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Listar reportes disponibles para usuario
                JsonObject reportes = new JsonObject();
                reportes.addProperty("historialGastos", "/api/transacciones");  // Ya existe
                reportes.addProperty("analisisBiblioteca", "/api/reportes/usuario/analisis-biblioteca");
                reportes.addProperty("usoBibliotecaFamiliar", "/api/reportes/usuario/uso-biblioteca-familiar");
                out.print(gson.toJson(reportes));
                
            } else if (pathInfo.equals("/analisis-biblioteca")) {
                // Análisis de biblioteca
                reporteAnalisisBiblioteca(request, response, usuarioId);
                
            } else if (pathInfo.equals("/uso-biblioteca-familiar")) {
                // Uso de biblioteca familiar
                reporteUsoBibliotecaFamiliar(request, response, usuarioId);
                
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\": \"Reporte no encontrado\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error interno del servidor\"}");
            e.printStackTrace();
        }
    }
    
    private void reporteAnalisisBiblioteca(HttpServletRequest request, HttpServletResponse response, int usuarioId) 
            throws IOException, SQLException {
        
        PrintWriter out = response.getWriter();
        
        // 1. Juegos con mayor valoración personal vs comunidad
        String sqlComparacion = "SELECT v.id, v.titulo, " +
                               "  v.calificacion_promedio as calificacion_comunidad, " +
                               "  c.estrellas as calificacion_personal " +
                               "FROM biblioteca b " +
                               "JOIN videojuego v ON b.videojuego_id = v.id " +
                               "LEFT JOIN calificacion c ON v.id = c.videojuego_id AND c.usuario_id = ? " +
                               "WHERE b.usuario_id = ? " +
                               "ORDER BY ABS(v.calificacion_promedio - COALESCE(c.estrellas, 0)) DESC " +
                               "LIMIT 10";
        
        // 2. Categorías favoritas
        String sqlCategorias = "SELECT cat.nombre, COUNT(*) as total_juegos " +
                              "FROM biblioteca b " +
                              "JOIN videojuego_categoria vc ON b.videojuego_id = vc.videojuego_id " +
                              "JOIN categoria cat ON vc.categoria_id = cat.id " +
                              "WHERE b.usuario_id = ? " +
                              "GROUP BY cat.id, cat.nombre " +
                              "ORDER BY total_juegos DESC";
        
        // 3. Total gastado
        String sqlTotalGastado = "SELECT SUM(precio_pagado) as total_gastado " +
                                "FROM transaccion " +
                                "WHERE usuario_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            JsonObject resultado = new JsonObject();
            
            // Comparación calificaciones
            try (PreparedStatement stmt = conn.prepareStatement(sqlComparacion)) {
                stmt.setInt(1, usuarioId);
                stmt.setInt(2, usuarioId);
                ResultSet rs = stmt.executeQuery();
                
                JsonArray comparaciones = new JsonArray();
                
                while (rs.next()) {
                    JsonObject juego = new JsonObject();
                    juego.addProperty("id", rs.getInt("id"));
                    juego.addProperty("titulo", rs.getString("titulo"));
                    juego.addProperty("calificacionComunidad", rs.getDouble("calificacion_comunidad"));
                    juego.addProperty("calificacionPersonal", rs.getInt("calificacion_personal"));
                    comparaciones.add(juego);
                }
                resultado.add("comparacionCalificaciones", comparaciones);
            }
            
            // Categorías favoritas
            try (PreparedStatement stmt = conn.prepareStatement(sqlCategorias)) {
                stmt.setInt(1, usuarioId);
                ResultSet rs = stmt.executeQuery();
                
                JsonArray categorias = new JsonArray();
                
                while (rs.next()) {
                    JsonObject categoria = new JsonObject();
                    categoria.addProperty("nombre", rs.getString("nombre"));
                    categoria.addProperty("totalJuegos", rs.getInt("total_juegos"));
                    categorias.add(categoria);
                }
                resultado.add("categoriasFavoritas", categorias);
            }
            
            // Total gastado
            try (PreparedStatement stmt = conn.prepareStatement(sqlTotalGastado)) {
                stmt.setInt(1, usuarioId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    resultado.addProperty("totalGastado", rs.getDouble("total_gastado"));
                } else {
                    resultado.addProperty("totalGastado", 0);
                }
            }
            
            resultado.addProperty("fechaGeneracion", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            out.print(gson.toJson(resultado));
        }
    }
    
    private void reporteUsoBibliotecaFamiliar(HttpServletRequest request, HttpServletResponse response, int usuarioId) 
            throws IOException, SQLException {
        
        PrintWriter out = response.getWriter();
        
        // Verificar si pertenece a un grupo familiar
        String sqlVerificarGrupo = "SELECT COUNT(*) as tiene_grupo FROM miembro_grupo WHERE usuario_id = ? AND estado = 'ACTIVO'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlVerificarGrupo)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt("tiene_grupo") == 0) {
                // No pertenece a un grupo
                JsonObject resultado = new JsonObject();
                resultado.addProperty("mensaje", "No perteneces a un grupo familiar");
                resultado.addProperty("fechaGeneracion", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                out.print(gson.toJson(resultado));
                return;
            }
        }
        
        // 1. Juegos prestados instalados más veces
        String sqlMasInstalados = "SELECT v.id, v.titulo, " +
                                 "  COUNT(p.id) as veces_instalado, " +
                                 "  u.nickname as propietario " +
                                 "FROM prestamo p " +
                                 "JOIN videojuego v ON p.videojuego_id = v.id " +
                                 "JOIN biblioteca b ON v.id = b.videojuego_id " +
                                 "JOIN usuario u ON b.usuario_id = u.id " +
                                 "WHERE p.usuario_id = ? AND p.estado = 'INSTALADO' " +
                                 "GROUP BY v.id, v.titulo, u.nickname " +
                                 "ORDER BY veces_instalado DESC " +
                                 "LIMIT 10";
        
        // 2. Juegos con más tiempo instalado (simulado)
        String sqlMasTiempo = "SELECT v.id, v.titulo, " +
                             "  COUNT(p.id) as veces_instalado, " +
                             "  u.nickname as propietario " +
                             "FROM prestamo p " +
                             "JOIN videojuego v ON p.videojuego_id = v.id " +
                             "JOIN biblioteca b ON v.id = b.videojuego_id " +
                             "JOIN usuario u ON b.usuario_id = u.id " +
                             "WHERE p.usuario_id = ? " +
                             "GROUP BY v.id, v.titulo, u.nickname " +
                             "ORDER BY veces_instalado DESC " +
                             "LIMIT 10";
        
        // 3. Calificación de la comunidad de juegos prestados
        String sqlCalificacionComunidad = "SELECT DISTINCT v.id, v.titulo, " +
                                         "  v.calificacion_promedio, " +
                                         "  u.nickname as propietario " +
                                         "FROM prestamo p " +
                                         "JOIN videojuego v ON p.videojuego_id = v.id " +
                                         "JOIN biblioteca b ON v.id = b.videojuego_id " +
                                         "JOIN usuario u ON b.usuario_id = u.id " +
                                         "WHERE p.usuario_id = ? " +
                                         "ORDER BY v.calificacion_promedio DESC";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            JsonObject resultado = new JsonObject();
            
            // Más instalados
            try (PreparedStatement stmt = conn.prepareStatement(sqlMasInstalados)) {
                stmt.setInt(1, usuarioId);
                ResultSet rs = stmt.executeQuery();
                
                JsonArray masInstalados = new JsonArray();
                
                while (rs.next()) {
                    JsonObject juego = new JsonObject();
                    juego.addProperty("id", rs.getInt("id"));
                    juego.addProperty("titulo", rs.getString("titulo"));
                    juego.addProperty("vecesInstalado", rs.getInt("veces_instalado"));
                    juego.addProperty("propietario", rs.getString("propietario"));
                    masInstalados.add(juego);
                }
                resultado.add("masInstalados", masInstalados);
            }
            
            // Más tiempo (usamos misma lógica por ahora)
            try (PreparedStatement stmt = conn.prepareStatement(sqlMasTiempo)) {
                stmt.setInt(1, usuarioId);
                ResultSet rs = stmt.executeQuery();
                
                JsonArray masTiempo = new JsonArray();
                
                while (rs.next()) {
                    JsonObject juego = new JsonObject();
                    juego.addProperty("id", rs.getInt("id"));
                    juego.addProperty("titulo", rs.getString("titulo"));
                    juego.addProperty("vecesInstalado", rs.getInt("veces_instalado"));
                    juego.addProperty("propietario", rs.getString("propietario"));
                    masTiempo.add(juego);
                }
                resultado.add("masTiempo", masTiempo);
            }
            
            // Calificación comunidad
            try (PreparedStatement stmt = conn.prepareStatement(sqlCalificacionComunidad)) {
                stmt.setInt(1, usuarioId);
                ResultSet rs = stmt.executeQuery();
                
                JsonArray calificacionesComunidad = new JsonArray();
                
                while (rs.next()) {
                    JsonObject juego = new JsonObject();
                    juego.addProperty("id", rs.getInt("id"));
                    juego.addProperty("titulo", rs.getString("titulo"));
                    juego.addProperty("calificacionComunidad", rs.getDouble("calificacion_promedio"));
                    juego.addProperty("propietario", rs.getString("propietario"));
                    calificacionesComunidad.add(juego);
                }
                resultado.add("calificacionesComunidad", calificacionesComunidad);
            }
            
            resultado.addProperty("fechaGeneracion", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            out.print(gson.toJson(resultado));
        }
    }
}