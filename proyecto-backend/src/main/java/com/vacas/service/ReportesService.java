package com.vacas.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vacas.dao.ComentarioDAO;
import com.vacas.dao.EmpresaDAO;
import com.vacas.dao.TransaccionDAO;
import com.vacas.dao.UsuarioDAO;
import com.vacas.dao.VideojuegoDAO;
import com.vacas.util.ConexionBD;

public class ReportesService {
    private TransaccionDAO transaccionDAO;
    private EmpresaDAO empresaDAO;
    private VideojuegoDAO videojuegoDAO;
    private UsuarioDAO usuarioDAO;
    private ComentarioDAO comentarioDAO;
    
    public ReportesService() {
        this.transaccionDAO = new TransaccionDAO();
        this.empresaDAO = new EmpresaDAO();
        this.videojuegoDAO = new VideojuegoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.comentarioDAO = new ComentarioDAO();
    }
    
    // REPORTE DE GANANCIAS GLOBALES
    public Map<String, Object> generarReporteGanancias(String fechaInicio, String fechaFin) {
        Map<String, Object> reporte = new HashMap<>();
        
        double totalIngresos = transaccionDAO.obtenerTotalVentas(fechaInicio, fechaFin);
        double totalComisiones = transaccionDAO.obtenerTotalComisiones(fechaInicio, fechaFin);
        double gananciaEmpresas = totalIngresos - totalComisiones;
        
        reporte.put("periodo", fechaInicio + " a " + fechaFin);
        reporte.put("total_ingresos", totalIngresos);
        reporte.put("comision_plataforma", totalComisiones);
        reporte.put("ganancia_empresas", gananciaEmpresas);
        reporte.put("porcentaje_comision", (totalIngresos > 0) ? (totalComisiones / totalIngresos) * 100 : 0);
        
        return reporte;
    }
    
    //  REPORTE DE INGRESOS POR EMPRESA
    public List<Map<String, Object>> generarReporteIngresosPorEmpresa(String fechaInicio, String fechaFin) {
        List<Map<String, Object>> reporte = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, SUM(t.monto_empresa) as ingresos_empresa, " +
                     "SUM(t.monto_comision) as comision_plataforma, SUM(t.precio_pagado) as ventas_totales " +
                     "FROM transaccion t " +
                     "JOIN videojuego v ON t.videojuego_id = v.id " +
                     "JOIN empresa e ON v.empresa_id = e.id " +
                     "WHERE t.fecha_compra BETWEEN ? AND ? " +
                     "GROUP BY e.id, e.nombre " +
                     "ORDER BY ventas_totales DESC";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> empresaData = new HashMap<>();
                empresaData.put("empresa_id", rs.getInt("id"));
                empresaData.put("empresa_nombre", rs.getString("nombre"));
                empresaData.put("ventas_totales", rs.getDouble("ventas_totales"));
                empresaData.put("ingresos_empresa", rs.getDouble("ingresos_empresa"));
                empresaData.put("comision_plataforma", rs.getDouble("comision_plataforma"));
                empresaData.put("porcentaje_comision", 
                    (rs.getDouble("ventas_totales") > 0) ? 
                    (rs.getDouble("comision_plataforma") / rs.getDouble("ventas_totales")) * 100 : 0);
                
                reporte.add(empresaData);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Error generando reporte por empresa: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reporte;
    }
    
    //  TOP VENTAS Y CALIDAD 
    public List<Map<String, Object>> generarTopVentasCalidad(int limite) {
        List<Map<String, Object>> top = new ArrayList<>();
       //...
        String sql = "SELECT v.id, v.titulo, e.nombre as empresa_nombre, " +
                     "COUNT(t.id) as total_ventas, " +
                     "COALESCE(vc.promedio, 0) as calificacion_promedio, " +
                     "COALESCE(vc.total_calificaciones, 0) as total_calificaciones " +
                     "FROM videojuego v " +
                     "LEFT JOIN transaccion t ON v.id = t.videojuego_id " +
                     "LEFT JOIN videojuego_calificacion vc ON v.id = vc.videojuego_id " +
                     "JOIN empresa e ON v.empresa_id = e.id " +
                     "WHERE v.disponible = true " +
                     "GROUP BY v.id, v.titulo, e.nombre, vc.promedio, vc.total_calificaciones " +
                     "HAVING total_calificaciones >= 3 OR total_calificaciones = 0 " + 
                     "ORDER BY (COUNT(t.id) * 0.6 + COALESCE(vc.promedio, 0) * 0.4) DESC " +
                     "LIMIT ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> juego = new HashMap<>();
                juego.put("videojuego_id", rs.getInt("id"));
                juego.put("titulo", rs.getString("titulo"));
                juego.put("empresa_nombre", rs.getString("empresa_nombre"));
                juego.put("total_ventas", rs.getInt("total_ventas"));
                juego.put("calificacion_promedio", rs.getDouble("calificacion_promedio"));
                juego.put("total_calificaciones", rs.getInt("total_calificaciones"));
                
                double puntaje = (rs.getInt("total_ventas") * 0.6) + (rs.getDouble("calificacion_promedio") * 0.4);
                juego.put("puntaje_balanceado", puntaje);
                
                top.add(juego);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println(" Error generando top ventas/calidad: " + e.getMessage());
            e.printStackTrace();
        }
        
        return top;
    }
    
    //  RANKING DE USUARIOS
    public Map<String, List<Map<String, Object>>> generarRankingUsuarios() {
        Map<String, List<Map<String, Object>>> ranking = new HashMap<>();
        
        // Usuarios con más juegos comprados
        String sqlCompras = "SELECT u.id, u.nickname, COUNT(t.id) as total_compras, SUM(t.precio_pagado) as total_gastado " +
                           "FROM usuario u " +
                           "LEFT JOIN transaccion t ON u.id = t.usuario_id " +
                           "WHERE u.tipo = 'USUARIO' " +
                           "GROUP BY u.id, u.nickname " +
                           "ORDER BY total_compras DESC, total_gastado DESC " +
                           "LIMIT 10";
        
        // Usuarios con más reseñas escritas
        String sqlResenas = "SELECT u.id, u.nickname, COUNT(c.id) as total_resenas, " +
                           "AVG(c.calificacion) as promedio_calificacion " +
                           "FROM usuario u " +
                           "LEFT JOIN comentario c ON u.id = c.usuario_id " +
                           "WHERE u.tipo = 'USUARIO' AND c.texto IS NOT NULL AND c.texto != '' " +
                           "GROUP BY u.id, u.nickname " +
                           "ORDER BY total_resenas DESC " +
                           "LIMIT 10";
        
        try (Connection conn = ConexionBD.getConnection()) {
            // Ranking por compras
            List<Map<String, Object>> rankingCompras = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlCompras)) {
                
                while (rs.next()) {
                    Map<String, Object> usuario = new HashMap<>();
                    usuario.put("usuario_id", rs.getInt("id"));
                    usuario.put("nickname", rs.getString("nickname"));
                    usuario.put("total_compras", rs.getInt("total_compras"));
                    usuario.put("total_gastado", rs.getDouble("total_gastado"));
                    rankingCompras.add(usuario);
                }
            }
            
            // Ranking por reseñas
            List<Map<String, Object>> rankingResenas = new ArrayList<>();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sqlResenas)) {
                
                while (rs.next()) {
                    Map<String, Object> usuario = new HashMap<>();
                    usuario.put("usuario_id", rs.getInt("id"));
                    usuario.put("nickname", rs.getString("nickname"));
                    usuario.put("total_resenas", rs.getInt("total_resenas"));
                    usuario.put("promedio_calificacion", rs.getDouble("promedio_calificacion"));
                    rankingResenas.add(usuario);
                }
            }
            
            ranking.put("ranking_compras", rankingCompras);
            ranking.put("ranking_resenas", rankingResenas);
            
        } catch (SQLException e) {
            System.err.println(" Error generando ranking de usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ranking;
    }
    
    // REPORTE DE VENTAS PROPIAS (para empresa)
    public List<Map<String, Object>> generarReporteVentasPropias(int empresaId, String fechaInicio, String fechaFin) {
        List<Map<String, Object>> reporte = new ArrayList<>();
        
        String sql = "SELECT v.id, v.titulo, COUNT(t.id) as total_ventas, " +
                     "SUM(t.precio_pagado) as ventas_totales, " +
                     "SUM(t.monto_comision) as comision_plataforma, " +
                     "SUM(t.monto_empresa) as ingresos_netos, " +
                     "COALESCE(vc.promedio, 0) as calificacion_promedio " +
                     "FROM videojuego v " +
                     "LEFT JOIN transaccion t ON v.id = t.videojuego_id " +
                     "LEFT JOIN videojuego_calificacion vc ON v.id = vc.videojuego_id " +
                     "WHERE v.empresa_id = ? " +
                     "AND (t.fecha_compra BETWEEN ? AND ? OR t.id IS NULL) " +
                     "GROUP BY v.id, v.titulo, vc.promedio " +
                     "ORDER BY ventas_totales DESC";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, empresaId);
            ps.setString(2, fechaInicio);
            ps.setString(3, fechaFin);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> juego = new HashMap<>();
                juego.put("videojuego_id", rs.getInt("id"));
                juego.put("titulo", rs.getString("titulo"));
                juego.put("total_ventas", rs.getInt("total_ventas"));
                juego.put("ventas_totales", rs.getDouble("ventas_totales"));
                juego.put("comision_plataforma", rs.getDouble("comision_plataforma"));
                juego.put("ingresos_netos", rs.getDouble("ingresos_netos"));
                juego.put("calificacion_promedio", rs.getDouble("calificacion_promedio"));
                
                reporte.add(juego);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error generando reporte ventas propias: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reporte;
    }
    
    // 6. REPORTE DE FEEDBACK (para empresa)
    public Map<String, Object> generarReporteFeedback(int empresaId) {
        Map<String, Object> reporte = new HashMap<>();
        
        // Juegos con calificaciones promedio
        String sqlCalificaciones = "SELECT v.id, v.titulo, COALESCE(vc.promedio, 0) as promedio, " +
                                  "COALESCE(vc.total_calificaciones, 0) as total_calificaciones " +
                                  "FROM videojuego v " +
                                  "LEFT JOIN videojuego_calificacion vc ON v.id = vc.videojuego_id " +
                                  "WHERE v.empresa_id = ? " +
                                  "ORDER BY vc.promedio DESC";
        
        // Mejores comentarios (más respuestas/interacciones)
        String sqlMejoresComentarios = "SELECT c.id, c.texto, u.nickname, v.titulo, " +
                                      "(SELECT COUNT(*) FROM comentario c2 WHERE c2.comentario_padre_id = c.id) as respuestas " +
                                      "FROM comentario c " +
                                      "JOIN usuario u ON c.usuario_id = u.id " +
                                      "JOIN videojuego v ON c.videojuego_id = v.id " +
                                      "WHERE v.empresa_id = ? AND c.comentario_padre_id IS NULL " +
                                      "ORDER BY respuestas DESC " +
                                      "LIMIT 5";
        
        // Peores calificaciones
        String sqlPeoresCalificaciones = "SELECT v.id, v.titulo, COALESCE(vc.promedio, 0) as promedio " +
                                        "FROM videojuego v " +
                                        "LEFT JOIN videojuego_calificacion vc ON v.id = vc.videojuego_id " +
                                        "WHERE v.empresa_id = ? AND vc.promedio < 3.0 " +
                                        "ORDER BY vc.promedio ASC " +
                                        "LIMIT 5";
        
        try (Connection conn = ConexionBD.getConnection()) {
            // Calificaciones promedio
            List<Map<String, Object>> calificaciones = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlCalificaciones)) {
                ps.setInt(1, empresaId);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    Map<String, Object> juego = new HashMap<>();
                    juego.put("videojuego_id", rs.getInt("id"));
                    juego.put("titulo", rs.getString("titulo"));
                    juego.put("calificacion_promedio", rs.getDouble("promedio"));
                    juego.put("total_calificaciones", rs.getInt("total_calificaciones"));
                    calificaciones.add(juego);
                }
                rs.close();
            }
            
            // Mejores comentarios
            List<Map<String, Object>> mejoresComentarios = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlMejoresComentarios)) {
                ps.setInt(1, empresaId);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    Map<String, Object> comentario = new HashMap<>();
                    comentario.put("comentario_id", rs.getInt("id"));
                    comentario.put("texto", rs.getString("texto"));
                    comentario.put("usuario_nickname", rs.getString("nickname"));
                    comentario.put("videojuego_titulo", rs.getString("titulo"));
                    comentario.put("respuestas", rs.getInt("respuestas"));
                    mejoresComentarios.add(comentario);
                }
                rs.close();
            }
            
            // Peores calificaciones
            List<Map<String, Object>> peoresCalificaciones = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlPeoresCalificaciones)) {
                ps.setInt(1, empresaId);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    Map<String, Object> juego = new HashMap<>();
                    juego.put("videojuego_id", rs.getInt("id"));
                    juego.put("titulo", rs.getString("titulo"));
                    juego.put("calificacion_promedio", rs.getDouble("promedio"));
                    peoresCalificaciones.add(juego);
                }
                rs.close();
            }
            
            reporte.put("calificaciones_promedio", calificaciones);
            reporte.put("mejores_comentarios", mejoresComentarios);
            reporte.put("peores_calificaciones", peoresCalificaciones);
            
        } catch (SQLException e) {
            System.err.println(" Error generando reporte feedback: " + e.getMessage());
            e.printStackTrace();
        }
        
        return reporte;
    }
    
    // TOP 5 JUEGOS POR EMPRESA (
    public List<Map<String, Object>> generarTop5JuegosEmpresa(int empresaId, String fechaInicio, String fechaFin) {
        List<Map<String, Object>> top = new ArrayList<>();
        
        String sql = "SELECT v.id, v.titulo, COUNT(t.id) as ventas, " +
                     "SUM(t.precio_pagado) as ingresos, " +
                     "COALESCE(vc.promedio, 0) as calificacion_promedio " +
                     "FROM videojuego v " +
                     "LEFT JOIN transaccion t ON v.id = t.videojuego_id " +
                     "LEFT JOIN videojuego_calificacion vc ON v.id = vc.videojuego_id " +
                     "WHERE v.empresa_id = ? " +
                     "AND (t.fecha_compra BETWEEN ? AND ? OR t.id IS NULL) " +
                     "GROUP BY v.id, v.titulo, vc.promedio " +
                     "ORDER BY ventas DESC " +
                     "LIMIT 5";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, empresaId);
            ps.setString(2, fechaInicio);
            ps.setString(3, fechaFin);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> juego = new HashMap<>();
                juego.put("videojuego_id", rs.getInt("id"));
                juego.put("titulo", rs.getString("titulo"));
                juego.put("ventas", rs.getInt("ventas"));
                juego.put("ingresos", rs.getDouble("ingresos"));
                juego.put("calificacion_promedio", rs.getDouble("calificacion_promedio"));
                top.add(juego);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error generando top 5 juegos empresa: " + e.getMessage());
            e.printStackTrace();
        }
        
        return top;
    }
    
    // HISTORIAL DE GASTOS DE USUARIO
    public List<Map<String, Object>> generarHistorialGastosUsuario(int usuarioId) {
        List<Map<String, Object>> historial = new ArrayList<>();
        
        String sql = "SELECT t.id, t.fecha_compra, v.titulo, t.precio_pagado, " +
                     "e.nombre as empresa_nombre " +
                     "FROM transaccion t " +
                     "JOIN videojuego v ON t.videojuego_id = v.id " +
                     "JOIN empresa e ON v.empresa_id = e.id " +
                     "WHERE t.usuario_id = ? " +
                     "ORDER BY t.fecha_compra DESC";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> compra = new HashMap<>();
                compra.put("transaccion_id", rs.getInt("id"));
                compra.put("fecha_compra", rs.getTimestamp("fecha_compra").toString());
                compra.put("videojuego_titulo", rs.getString("titulo"));
                compra.put("empresa_nombre", rs.getString("empresa_nombre"));
                compra.put("monto", rs.getDouble("precio_pagado"));
                historial.add(compra);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error generando historial gastos usuario: " + e.getMessage());
            e.printStackTrace();
        }
        
        return historial;
    }
    
    // ANÁLISIS DE BIBLIOTECA DE USUARIO
    public Map<String, Object> generarAnalisisBibliotecaUsuario(int usuarioId) {
        Map<String, Object> analisis = new HashMap<>();
        
        // Juegos con mayor valoración comunitaria vs personal
        String sqlValoraciones = "SELECT v.id, v.titulo, COALESCE(vc.promedio, 0) as calificacion_comunidad, " +
                                "c.calificacion as calificacion_personal " +
                                "FROM transaccion t " +
                                "JOIN videojuego v ON t.videojuego_id = v.id " +
                                "LEFT JOIN videojuego_calificacion vc ON v.id = vc.videojuego_id " +
                                "LEFT JOIN comentario c ON v.id = c.videojuego_id AND c.usuario_id = t.usuario_id " +
                                "WHERE t.usuario_id = ? AND c.calificacion IS NOT NULL " +
                                "ORDER BY ABS(vc.promedio - c.calificacion) DESC " +
                                "LIMIT 5";
        
        // Categorías favoritas 
        String sqlCategoriasFavoritas = "SELECT c.nombre, COUNT(*) as total_juegos " +
                                       "FROM transaccion t " +
                                       "JOIN videojuego v ON t.videojuego_id = v.id " +
                                       "JOIN videojuego_categoria vc ON v.id = vc.videojuego_id " +
                                       "JOIN categoria c ON vc.categoria_id = c.id " +
                                       "WHERE t.usuario_id = ? " +
                                       "GROUP BY c.id, c.nombre " +
                                       "ORDER BY total_juegos DESC " +
                                       "LIMIT 5";
        
        try (Connection conn = ConexionBD.getConnection()) {
            List<Map<String, Object>> valoraciones = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlValoraciones)) {
                ps.setInt(1, usuarioId);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    Map<String, Object> juego = new HashMap<>();
                    juego.put("videojuego_id", rs.getInt("id"));
                    juego.put("titulo", rs.getString("titulo"));
                    juego.put("calificacion_comunidad", rs.getDouble("calificacion_comunidad"));
                    juego.put("calificacion_personal", rs.getInt("calificacion_personal"));
                    juego.put("diferencia", Math.abs(rs.getDouble("calificacion_comunidad") - rs.getInt("calificacion_personal")));
                    valoraciones.add(juego);
                }
                rs.close();
            }
            
            List<Map<String, Object>> categoriasFavoritas = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlCategoriasFavoritas)) {
                ps.setInt(1, usuarioId);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    Map<String, Object> categoria = new HashMap<>();
                    categoria.put("nombre", rs.getString("nombre"));
                    categoria.put("total_juegos", rs.getInt("total_juegos"));
                    categoriasFavoritas.add(categoria);
                }
                rs.close();
            }
            
            analisis.put("comparacion_valoraciones", valoraciones);
            analisis.put("categorias_favoritas", categoriasFavoritas);
            analisis.put("usuario_id", usuarioId);
            
        } catch (SQLException e) {
            System.err.println("Error generando análisis biblioteca: " + e.getMessage());
            e.printStackTrace();
        }
        
        return analisis;
    }
}