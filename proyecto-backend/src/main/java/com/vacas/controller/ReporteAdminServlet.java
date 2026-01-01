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

@WebServlet("/api/reportes/admin/*")
public class ReporteAdminServlet extends HttpServlet {
    
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        // Verificar que sea administrador
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("tipoUsuario"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"No autorizado\"}");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // Listar reportes disponibles
                JsonObject reportes = new JsonObject();
                reportes.addProperty("gananciasGlobales", "/api/reportes/admin/ganancias-globales");
                reportes.addProperty("topVentasCalidad", "/api/reportes/admin/top-ventas-calidad");
                reportes.addProperty("ingresosPorEmpresa", "/api/reportes/admin/ingresos-empresa");
                reportes.addProperty("rankingUsuarios", "/api/reportes/admin/ranking-usuarios");
                out.print(gson.toJson(reportes));
                
            } else if (pathInfo.equals("/ganancias-globales")) {
                // Reporte de ganancias globales
                reporteGananciasGlobales(request, response);
                
            } else if (pathInfo.equals("/top-ventas-calidad")) {
                // Reporte top ventas y calidad
                reporteTopVentasCalidad(request, response);
                
            } else if (pathInfo.equals("/ingresos-empresa")) {
                // Reporte ingresos por empresa
                reporteIngresosPorEmpresa(request, response);
                
            } else if (pathInfo.equals("/ranking-usuarios")) {
                // Reporte ranking de usuarios
                reporteRankingUsuarios(request, response);
                
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
    
    private void reporteGananciasGlobales(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        PrintWriter out = response.getWriter();
        
        // Parámetros opcionales de fechas
        String fechaInicioStr = request.getParameter("fechaInicio");
        String fechaFinStr = request.getParameter("fechaFin");
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append("  SUM(precio_pagado) as total_ingresos, ");
        sql.append("  SUM(monto_comision) as total_comisiones, ");
        sql.append("  SUM(precio_pagado - monto_comision) as total_empresas, ");
        sql.append("  COUNT(*) as total_ventas ");
        sql.append("FROM transaccion ");
        
        if (fechaInicioStr != null || fechaFinStr != null) {
            sql.append("WHERE 1=1 ");
            if (fechaInicioStr != null) {
                sql.append("AND fecha_compra >= ? ");
            }
            if (fechaFinStr != null) {
                sql.append("AND fecha_compra <= ? ");
            }
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (fechaInicioStr != null) {
                stmt.setString(paramIndex++, fechaInicioStr);
            }
            if (fechaFinStr != null) {
                stmt.setString(paramIndex++, fechaFinStr);
            }
            
            ResultSet rs = stmt.executeQuery();
            JsonObject resultado = new JsonObject();
            
            if (rs.next()) {
                resultado.addProperty("totalIngresos", rs.getDouble("total_ingresos"));
                resultado.addProperty("totalComisiones", rs.getDouble("total_comisiones"));
                resultado.addProperty("totalEmpresas", rs.getDouble("total_empresas"));
                resultado.addProperty("totalVentas", rs.getInt("total_ventas"));
                resultado.addProperty("fechaGeneracion", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                
                if (fechaInicioStr != null) resultado.addProperty("fechaInicio", fechaInicioStr);
                if (fechaFinStr != null) resultado.addProperty("fechaFin", fechaFinStr);
            }
            
            out.print(gson.toJson(resultado));
        }
    }
    
    private void reporteTopVentasCalidad(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        PrintWriter out = response.getWriter();
        
        // Parámetros opcionales
        String categoriaId = request.getParameter("categoriaId");
        String edadMinima = request.getParameter("edadMinima");
        
        // 1. Videojuegos más vendidos
        StringBuilder sqlVentas = new StringBuilder();
        sqlVentas.append("SELECT v.id, v.titulo, e.nombre as empresa, ");
        sqlVentas.append("  COUNT(t.id) as ventas, ");
        sqlVentas.append("  SUM(t.precio_pagado) as ingresos ");
        sqlVentas.append("FROM videojuego v ");
        sqlVentas.append("LEFT JOIN transaccion t ON v.id = t.videojuego_id ");
        sqlVentas.append("LEFT JOIN empresa e ON v.empresa_id = e.id ");
        sqlVentas.append("WHERE v.disponible = TRUE ");
        
        if (categoriaId != null) {
            sqlVentas.append("AND v.id IN (SELECT videojuego_id FROM videojuego_categoria WHERE categoria_id = ?) ");
        }
        if (edadMinima != null) {
            sqlVentas.append("AND v.edad_minima = ? ");
        }
        
        sqlVentas.append("GROUP BY v.id, v.titulo, e.nombre ");
        sqlVentas.append("ORDER BY ventas DESC ");
        sqlVentas.append("LIMIT 10");
        
        // 2. Videojuegos mejor calificados
        StringBuilder sqlCalificados = new StringBuilder();
        sqlCalificados.append("SELECT v.id, v.titulo, e.nombre as empresa, ");
        sqlCalificados.append("  v.calificacion_promedio, ");
        sqlCalificados.append("  COUNT(c.id) as total_calificaciones ");
        sqlCalificados.append("FROM videojuego v ");
        sqlCalificados.append("LEFT JOIN empresa e ON v.empresa_id = e.id ");
        sqlCalificados.append("LEFT JOIN calificacion c ON v.id = c.videojuego_id ");
        sqlCalificados.append("WHERE v.disponible = TRUE ");
        sqlCalificados.append("  AND v.calificacion_promedio > 0 ");
        
        if (categoriaId != null) {
            sqlCalificados.append("AND v.id IN (SELECT videojuego_id FROM videojuego_categoria WHERE categoria_id = ?) ");
        }
        if (edadMinima != null) {
            sqlCalificados.append("AND v.edad_minima = ? ");
        }
        
        sqlCalificados.append("GROUP BY v.id, v.titulo, e.nombre, v.calificacion_promedio ");
        sqlCalificados.append("ORDER BY v.calificacion_promedio DESC ");
        sqlCalificados.append("LIMIT 10");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            JsonObject resultado = new JsonObject();
            
            // Ejecutar query de más vendidos
            try (PreparedStatement stmt = conn.prepareStatement(sqlVentas.toString())) {
                int paramIndex = 1;
                if (categoriaId != null) {
                    stmt.setInt(paramIndex++, Integer.parseInt(categoriaId));
                }
                if (edadMinima != null) {
                    stmt.setInt(paramIndex++, Integer.parseInt(edadMinima));
                }
                
                ResultSet rs = stmt.executeQuery();
                JsonArray masVendidos = new JsonArray();
                
                while (rs.next()) {
                    JsonObject juego = new JsonObject();
                    juego.addProperty("id", rs.getInt("id"));
                    juego.addProperty("titulo", rs.getString("titulo"));
                    juego.addProperty("empresa", rs.getString("empresa"));
                    juego.addProperty("ventas", rs.getInt("ventas"));
                    juego.addProperty("ingresos", rs.getDouble("ingresos"));
                    masVendidos.add(juego);
                }
                resultado.add("masVendidos", masVendidos);
            }
            
            // Ejecutar query de mejor calificados
            try (PreparedStatement stmt = conn.prepareStatement(sqlCalificados.toString())) {
                int paramIndex = 1;
                if (categoriaId != null) {
                    stmt.setInt(paramIndex++, Integer.parseInt(categoriaId));
                }
                if (edadMinima != null) {
                    stmt.setInt(paramIndex++, Integer.parseInt(edadMinima));
                }
                
                ResultSet rs = stmt.executeQuery();
                JsonArray mejorCalificados = new JsonArray();
                
                while (rs.next()) {
                    JsonObject juego = new JsonObject();
                    juego.addProperty("id", rs.getInt("id"));
                    juego.addProperty("titulo", rs.getString("titulo"));
                    juego.addProperty("empresa", rs.getString("empresa"));
                    juego.addProperty("calificacionPromedio", rs.getDouble("calificacion_promedio"));
                    juego.addProperty("totalCalificaciones", rs.getInt("total_calificaciones"));
                    mejorCalificados.add(juego);
                }
                resultado.add("mejorCalificados", mejorCalificados);
            }
            
            resultado.addProperty("fechaGeneracion", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            out.print(gson.toJson(resultado));
        }
    }
    
    private void reporteIngresosPorEmpresa(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        PrintWriter out = response.getWriter();
        
        String sql = "SELECT e.id, e.nombre, " +
                    "  COUNT(t.id) as total_ventas, " +
                    "  SUM(t.precio_pagado) as ventas_brutas, " +
                    "  SUM(t.monto_comision) as comisiones_retenidas, " +
                    "  SUM(t.precio_pagado - t.monto_comision) as ingreso_neto " +
                    "FROM empresa e " +
                    "LEFT JOIN videojuego v ON e.id = v.empresa_id " +
                    "LEFT JOIN transaccion t ON v.id = t.videojuego_id " +
                    "GROUP BY e.id, e.nombre " +
                    "ORDER BY ventas_brutas DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            JsonArray empresas = new JsonArray();
            
            while (rs.next()) {
                JsonObject empresa = new JsonObject();
                empresa.addProperty("id", rs.getInt("id"));
                empresa.addProperty("nombre", rs.getString("nombre"));
                empresa.addProperty("totalVentas", rs.getInt("total_ventas"));
                empresa.addProperty("ventasBrutas", rs.getDouble("ventas_brutas"));
                empresa.addProperty("comisionesRetenidas", rs.getDouble("comisiones_retenidas"));
                empresa.addProperty("ingresoNeto", rs.getDouble("ingreso_neto"));
                empresas.add(empresa);
            }
            
            JsonObject resultado = new JsonObject();
            resultado.add("empresas", empresas);
            resultado.addProperty("fechaGeneracion", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            resultado.addProperty("totalEmpresas", empresas.size());
            
            out.print(gson.toJson(resultado));
        }
    }
    
    private void reporteRankingUsuarios(HttpServletRequest request, HttpServletResponse response) 
            throws IOException, SQLException {
        
        PrintWriter out = response.getWriter();
        
        // 1. Usuarios con más juegos comprados
        String sqlCompras = "SELECT u.id, u.nickname, u.correo, u.pais, " +
                           "  COUNT(DISTINCT b.videojuego_id) as juegos_comprados, " +
                           "  SUM(t.precio_pagado) as total_gastado " +
                           "FROM usuario u " +
                           "LEFT JOIN biblioteca b ON u.id = b.usuario_id " +
                           "LEFT JOIN transaccion t ON u.id = t.usuario_id " +
                           "WHERE u.tipo_usuario = 'COMUN' " +
                           "GROUP BY u.id, u.nickname, u.correo, u.pais " +
                           "ORDER BY juegos_comprados DESC " +
                           "LIMIT 10";
        
        // 2. Usuarios con más reseñas escritas
        String sqlResenas = "SELECT u.id, u.nickname, u.correo, u.pais, " +
                           "  COUNT(c.id) as total_resenas, " +
                           "  COUNT(DISTINCT c.videojuego_id) as juegos_comentados " +
                           "FROM usuario u " +
                           "LEFT JOIN comentario c ON u.id = c.usuario_id " +
                           "WHERE u.tipo_usuario = 'COMUN' " +
                           "GROUP BY u.id, u.nickname, u.correo, u.pais " +
                           "ORDER BY total_resenas DESC " +
                           "LIMIT 10";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            JsonObject resultado = new JsonObject();
            
            // Top compradores
            try (PreparedStatement stmt = conn.prepareStatement(sqlCompras);
                 ResultSet rs = stmt.executeQuery()) {
                
                JsonArray topCompradores = new JsonArray();
                
                while (rs.next()) {
                    JsonObject usuario = new JsonObject();
                    usuario.addProperty("id", rs.getInt("id"));
                    usuario.addProperty("nickname", rs.getString("nickname"));
                    usuario.addProperty("correo", rs.getString("correo"));
                    usuario.addProperty("pais", rs.getString("pais"));
                    usuario.addProperty("juegosComprados", rs.getInt("juegos_comprados"));
                    usuario.addProperty("totalGastado", rs.getDouble("total_gastado"));
                    topCompradores.add(usuario);
                }
                resultado.add("topCompradores", topCompradores);
            }
            
            // Top reseñadores
            try (PreparedStatement stmt = conn.prepareStatement(sqlResenas);
                 ResultSet rs = stmt.executeQuery()) {
                
                JsonArray topResenadores = new JsonArray();
                
                while (rs.next()) {
                    JsonObject usuario = new JsonObject();
                    usuario.addProperty("id", rs.getInt("id"));
                    usuario.addProperty("nickname", rs.getString("nickname"));
                    usuario.addProperty("correo", rs.getString("correo"));
                    usuario.addProperty("pais", rs.getString("pais"));
                    usuario.addProperty("totalResenas", rs.getInt("total_resenas"));
                    usuario.addProperty("juegosComentados", rs.getInt("juegos_comentados"));
                    topResenadores.add(usuario);
                }
                resultado.add("topResenadores", topResenadores);
            }
            
            resultado.addProperty("fechaGeneracion", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            out.print(gson.toJson(resultado));
        }
    }
}