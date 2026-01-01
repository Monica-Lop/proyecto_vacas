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

@WebServlet("/api/reportes/empresa/*")
public class ReporteEmpresaServlet extends HttpServlet {
    
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // Verificar que sea empresa
        HttpSession session = request.getSession(false);
        if (session == null || !"EMPRESA".equals(session.getAttribute("tipoUsuario"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"No autorizado\"}");
            return;
        }
        
        int empresaId = (int) session.getAttribute("empresaId");
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Listar reportes disponibles para empresa
                JsonObject reportes = new JsonObject();
                reportes.addProperty("ventasPropias", "/api/reportes/empresa/ventas-propias");
                reportes.addProperty("feedback", "/api/reportes/empresa/feedback");
                reportes.addProperty("top5", "/api/reportes/empresa/top5");
                out.print(gson.toJson(reportes));
                
            } else if (pathInfo.equals("/ventas-propias")) {
                // Reporte de ventas propias
                reporteVentasPropias(request, response, empresaId);
                
            } else if (pathInfo.equals("/feedback")) {
                // Reporte de feedback
                reporteFeedback(request, response, empresaId);
                
            } else if (pathInfo.equals("/top5")) {
                // Top 5 juegos
                reporteTop5(request, response, empresaId);
                
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
    
    private void reporteVentasPropias(HttpServletRequest request, HttpServletResponse response, int empresaId) 
            throws IOException, SQLException {
        
        PrintWriter out = response.getWriter();
        
        String sql = "SELECT v.id, v.titulo, " +
                    "  COUNT(t.id) as total_ventas, " +
                    "  SUM(t.precio_pagado) as ventas_brutas, " +
                    "  SUM(t.monto_comision) as comisiones, " +
                    "  SUM(t.precio_pagado - t.monto_comision) as ingreso_neto " +
                    "FROM videojuego v " +
                    "LEFT JOIN transaccion t ON v.id = t.videojuego_id " +
                    "WHERE v.empresa_id = ? " +
                    "GROUP BY v.id, v.titulo " +
                    "ORDER BY ventas_brutas DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, empresaId);
            ResultSet rs = stmt.executeQuery();
            
            JsonArray videojuegos = new JsonArray();
            double totalVentasBrutas = 0;
            double totalComisiones = 0;
            double totalIngresoNeto = 0;
            int totalUnidades = 0;
            
            while (rs.next()) {
                JsonObject juego = new JsonObject();
                juego.addProperty("id", rs.getInt("id"));
                juego.addProperty("titulo", rs.getString("titulo"));
                juego.addProperty("totalVentas", rs.getInt("total_ventas"));
                juego.addProperty("ventasBrutas", rs.getDouble("ventas_brutas"));
                juego.addProperty("comisiones", rs.getDouble("comisiones"));
                juego.addProperty("ingresoNeto", rs.getDouble("ingreso_neto"));
                videojuegos.add(juego);
                
                totalVentasBrutas += rs.getDouble("ventas_brutas");
                totalComisiones += rs.getDouble("comisiones");
                totalIngresoNeto += rs.getDouble("ingreso_neto");
                totalUnidades += rs.getInt("total_ventas");
            }
            
            JsonObject resultado = new JsonObject();
            resultado.add("videojuegos", videojuegos);
            resultado.addProperty("totalVentasBrutas", totalVentasBrutas);
            resultado.addProperty("totalComisiones", totalComisiones);
            resultado.addProperty("totalIngresoNeto", totalIngresoNeto);
            resultado.addProperty("totalUnidades", totalUnidades);
            resultado.addProperty("fechaGeneracion", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            
            out.print(gson.toJson(resultado));
        }
    }
    
    private void reporteFeedback(HttpServletRequest request, HttpServletResponse response, int empresaId) 
            throws IOException, SQLException {
        
        PrintWriter out = response.getWriter();
        
        // 1. Calificaciones promedio por juego
        String sqlCalificaciones = "SELECT v.id, v.titulo, " +
                                  "  v.calificacion_promedio, " +
                                  "  COUNT(c.id) as total_calificaciones " +
                                  "FROM videojuego v " +
                                  "LEFT JOIN calificacion c ON v.id = c.videojuego_id " +
                                  "WHERE v.empresa_id = ? " +
                                  "GROUP BY v.id, v.titulo, v.calificacion_promedio " +
                                  "ORDER BY v.calificacion_promedio DESC";
        
        // 2. Mejores comentarios (con más respuestas)
        String sqlMejoresComentarios = "SELECT c.id, c.texto, c.fecha_comentario, " +
                                      "  u.nickname, v.titulo, " +
                                      "  (SELECT COUNT(*) FROM comentario c2 WHERE c2.comentario_padre_id = c.id) as respuestas " +
                                      "FROM comentario c " +
                                      "JOIN usuario u ON c.usuario_id = u.id " +
                                      "JOIN videojuego v ON c.videojuego_id = v.id " +
                                      "WHERE v.empresa_id = ? AND c.visible = TRUE " +
                                      "ORDER BY respuestas DESC, c.fecha_comentario DESC " +
                                      "LIMIT 10";
        
        // 3. Peores calificaciones
        String sqlPeoresCalificaciones = "SELECT v.id, v.titulo, " +
                                        "  MIN(c.estrellas) as peor_calificacion, " +
                                        "  AVG(c.estrellas) as promedio, " +
                                        "  COUNT(c.id) as total " +
                                        "FROM videojuego v " +
                                        "LEFT JOIN calificacion c ON v.id = c.videojuego_id " +
                                        "WHERE v.empresa_id = ? AND c.estrellas IS NOT NULL " +
                                        "GROUP BY v.id, v.titulo " +
                                        "HAVING MIN(c.estrellas) <= 2 " +
                                        "ORDER BY peor_calificacion ASC, promedio ASC " +
                                        "LIMIT 10";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            JsonObject resultado = new JsonObject();
            
            // Calificaciones promedio
            try (PreparedStatement stmt = conn.prepareStatement(sqlCalificaciones)) {
                stmt.setInt(1, empresaId);
                ResultSet rs = stmt.executeQuery();
                
                JsonArray calificaciones = new JsonArray();
                
                while (rs.next()) {
                    JsonObject juego = new JsonObject();
                    juego.addProperty("id", rs.getInt("id"));
                    juego.addProperty("titulo", rs.getString("titulo"));
                    juego.addProperty("calificacionPromedio", rs.getDouble("calificacion_promedio"));
                    juego.addProperty("totalCalificaciones", rs.getInt("total_calificaciones"));
                    calificaciones.add(juego);
                }
                resultado.add("calificaciones", calificaciones);
            }
            
            // Mejores comentarios
            try (PreparedStatement stmt = conn.prepareStatement(sqlMejoresComentarios)) {
                stmt.setInt(1, empresaId);
                ResultSet rs = stmt.executeQuery();
                
                JsonArray mejoresComentarios = new JsonArray();
                
                while (rs.next()) {
                    JsonObject comentario = new JsonObject();
                    comentario.addProperty("id", rs.getInt("id"));
                    comentario.addProperty("texto", rs.getString("texto"));
                    comentario.addProperty("fecha", rs.getString("fecha_comentario"));
                    comentario.addProperty("usuario", rs.getString("nickname"));
                    comentario.addProperty("videojuego", rs.getString("titulo"));
                    comentario.addProperty("respuestas", rs.getInt("respuestas"));
                    mejoresComentarios.add(comentario);
                }
                resultado.add("mejoresComentarios", mejoresComentarios);
            }
            
            // Peores calificaciones
            try (PreparedStatement stmt = conn.prepareStatement(sqlPeoresCalificaciones)) {
                stmt.setInt(1, empresaId);
                ResultSet rs = stmt.executeQuery();
                
                JsonArray peoresCalificaciones = new JsonArray();
                
                while (rs.next()) {
                    JsonObject juego = new JsonObject();
                    juego.addProperty("id", rs.getInt("id"));
                    juego.addProperty("titulo", rs.getString("titulo"));
                    juego.addProperty("peorCalificacion", rs.getInt("peor_calificacion"));
                    juego.addProperty("promedio", rs.getDouble("promedio"));
                    juego.addProperty("totalCalificaciones", rs.getInt("total"));
                    peoresCalificaciones.add(juego);
                }
                resultado.add("peoresCalificaciones", peoresCalificaciones);
            }
            
            resultado.addProperty("fechaGeneracion", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            out.print(gson.toJson(resultado));
        }
    }
    
    private void reporteTop5(HttpServletRequest request, HttpServletResponse response, int empresaId) 
            throws IOException, SQLException {
        
        PrintWriter out = response.getWriter();
        
        // Parámetros de fechas
        String fechaInicioStr = request.getParameter("fechaInicio");
        String fechaFinStr = request.getParameter("fechaFin");
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT v.id, v.titulo, ");
        sql.append("  COUNT(t.id) as ventas, ");
        sql.append("  SUM(t.precio_pagado) as ingresos ");
        sql.append("FROM videojuego v ");
        sql.append("LEFT JOIN transaccion t ON v.id = t.videojuego_id ");
        sql.append("WHERE v.empresa_id = ? ");
        
        if (fechaInicioStr != null) {
            sql.append("AND t.fecha_compra >= ? ");
        }
        if (fechaFinStr != null) {
            sql.append("AND t.fecha_compra <= ? ");
        }
        
        sql.append("GROUP BY v.id, v.titulo ");
        sql.append("ORDER BY ventas DESC ");
        sql.append("LIMIT 5");
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            stmt.setInt(paramIndex++, empresaId);
            
            if (fechaInicioStr != null) {
                stmt.setString(paramIndex++, fechaInicioStr);
            }
            if (fechaFinStr != null) {
                stmt.setString(paramIndex++, fechaFinStr);
            }
            
            ResultSet rs = stmt.executeQuery();
            JsonArray top5 = new JsonArray();
            
            while (rs.next()) {
                JsonObject juego = new JsonObject();
                juego.addProperty("id", rs.getInt("id"));
                juego.addProperty("titulo", rs.getString("titulo"));
                juego.addProperty("ventas", rs.getInt("ventas"));
                juego.addProperty("ingresos", rs.getDouble("ingresos"));
                top5.add(juego);
            }
            
            JsonObject resultado = new JsonObject();
            resultado.add("top5", top5);
            resultado.addProperty("fechaGeneracion", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            
            if (fechaInicioStr != null) resultado.addProperty("fechaInicio", fechaInicioStr);
            if (fechaFinStr != null) resultado.addProperty("fechaFin", fechaFinStr);
            
            out.print(gson.toJson(resultado));
        }
    }
}