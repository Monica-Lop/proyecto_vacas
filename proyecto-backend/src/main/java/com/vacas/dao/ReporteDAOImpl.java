package com.vacas.dao;

import com.vacas.model.*;
import com.vacas.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAOImpl implements ReporteDAO {
    
    @Override
    public List<Transaccion> obtenerTransaccionesPorUsuario(int usuarioId) {
        List<Transaccion> transacciones = new ArrayList<>();
        String sql = "SELECT * FROM transacciones WHERE usuario_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaccion transaccion = new Transaccion();
                transaccion.setId(rs.getInt("id"));
                transaccion.setMonto(rs.getDouble("monto"));
                transaccion.setFecha(rs.getTimestamp("fecha"));
                transaccion.setTipo(rs.getString("tipo"));
                transaccion.setDescripcion(rs.getString("descripcion"));
                transaccion.setUsuarioId(rs.getInt("usuario_id"));
                transaccion.setEmpresaId(rs.getInt("empresa_id"));
                transacciones.add(transaccion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transacciones;
    }
    
    @Override
    public List<Transaccion> obtenerTransaccionesPorVideojuego(int videojuegoId) {
        List<Transaccion> transacciones = new ArrayList<>();
        // Ajusta según tu esquema real
        String sql = "SELECT t.* FROM transacciones t " +
                    "JOIN detalle_transaccion dt ON t.id = dt.transaccion_id " +
                    "WHERE dt.videojuego_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, videojuegoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaccion transaccion = new Transaccion();
                transaccion.setId(rs.getInt("id"));
                transaccion.setMonto(rs.getDouble("monto"));
                transaccion.setFecha(rs.getTimestamp("fecha"));
                transaccion.setTipo(rs.getString("tipo"));
                transacciones.add(transaccion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transacciones;
    }
    
    @Override
    public List<Transaccion> obtenerTransaccionesPorEmpresa(int empresaId) {
        List<Transaccion> transacciones = new ArrayList<>();
        String sql = "SELECT * FROM transacciones WHERE empresa_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empresaId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaccion transaccion = new Transaccion();
                transaccion.setId(rs.getInt("id"));
                transaccion.setMonto(rs.getDouble("monto"));
                transaccion.setFecha(rs.getTimestamp("fecha"));
                transaccion.setTipo(rs.getString("tipo"));
                transacciones.add(transaccion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transacciones;
    }
    
    @Override
    public double obtenerReporteGananciasGlobales() {
        double total = 0.0;
        String sql = "SELECT COALESCE(SUM(monto), 0) as total FROM transacciones WHERE tipo = 'VENTA'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
    
    @Override
    public List<Videojuego> obtenerTopVideojuegos(int topN) {
        List<Videojuego> videojuegos = new ArrayList<>();
        // Ajusta según tu esquema real
        String sql = "SELECT v.*, COUNT(t.id) as total_ventas " +
                    "FROM videojuegos v " +
                    "LEFT JOIN transacciones t ON v.id = t.videojuego_id AND t.tipo = 'VENTA' " +
                    "GROUP BY v.id " +
                    "ORDER BY total_ventas DESC " +
                    "LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, topN);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Videojuego videojuego = new Videojuego();
                videojuego.setId(rs.getInt("id"));
                videojuego.setTitulo(rs.getString("titulo"));
                videojuego.setDescripcion(rs.getString("descripcion"));
                videojuego.setPrecio(rs.getDouble("precio"));
                videojuego.setImagenUrl(rs.getString("imagen_url"));
                videojuego.setEmpresaId(rs.getInt("empresa_id"));
                videojuego.setCategoriaId(rs.getInt("categoria_id"));
                videojuego.setFechaPublicacion(rs.getDate("fecha_publicacion"));
                videojuego.setStock(rs.getInt("stock"));
                videojuegos.add(videojuego);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videojuegos;
    }
    
    @Override
    public List<ReporteIngresosEmpresa> obtenerReporteIngresosPorEmpresa() {
        List<ReporteIngresosEmpresa> reportes = new ArrayList<>();
        String sql = "SELECT e.id, e.nombre, COALESCE(SUM(t.monto), 0) as total_ingresos " +
                    "FROM empresas e " +
                    "LEFT JOIN transacciones t ON e.id = t.empresa_id AND t.tipo = 'VENTA' " +
                    "GROUP BY e.id, e.nombre " +
                    "ORDER BY total_ingresos DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ReporteIngresosEmpresa reporte = new ReporteIngresosEmpresa();
                reporte.setIdEmpresa(rs.getInt("id"));
                reporte.setNombreEmpresa(rs.getString("nombre"));
                reporte.setTotalIngresos(rs.getDouble("total_ingresos"));
                reportes.add(reporte);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportes;
    }
    
    @Override
    public List<ReporteVentasEmpresa> obtenerReporteVentasPorEmpresa(int idEmpresa, java.util.Date fechaInicio, java.util.Date fechaFin) {
        List<ReporteVentasEmpresa> reportes = new ArrayList<>();
        String sql = "SELECT DATE(t.fecha) as fecha_dia, COUNT(*) as ventas, " +
                    "SUM(t.monto) as total, e.nombre as empresa_nombre " +
                    "FROM transacciones t " +
                    "INNER JOIN empresas e ON t.empresa_id = e.id " +
                    "WHERE t.empresa_id = ? AND t.fecha BETWEEN ? AND ? AND t.tipo = 'VENTA' " +
                    "GROUP BY DATE(t.fecha), e.nombre " +
                    "ORDER BY fecha_dia";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEmpresa);
            stmt.setTimestamp(2, new Timestamp(fechaInicio.getTime()));
            stmt.setTimestamp(3, new Timestamp(fechaFin.getTime()));
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                ReporteVentasEmpresa reporte = new ReporteVentasEmpresa();
                reporte.setFecha(new java.util.Date(rs.getDate("fecha_dia").getTime()));
                reporte.setCantidadVentas(rs.getInt("ventas"));
                reporte.setTotalVentas(rs.getDouble("total"));
                reporte.setIdEmpresa(idEmpresa);
                reporte.setNombreEmpresa(rs.getString("empresa_nombre"));
                reportes.add(reporte);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportes;
    }
    
    @Override
    public List<ReporteRankingUsuarios> obtenerRankingUsuarios() {
        List<ReporteRankingUsuarios> ranking = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.nombre, " +
                    "COUNT(t.id) as compras, " +
                    "COALESCE(SUM(t.monto), 0) as gastado " +
                    "FROM usuarios u " +
                    "LEFT JOIN transacciones t ON u.id = t.usuario_id AND t.tipo = 'COMPRA' " +
                    "GROUP BY u.id, u.username, u.nombre " +
                    "ORDER BY gastado DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ReporteRankingUsuarios usuario = new ReporteRankingUsuarios();
                usuario.setIdUsuario(rs.getInt("id"));
                usuario.setUsername(rs.getString("username"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setCantidadCompras(rs.getInt("compras"));
                usuario.setTotalGastado(rs.getDouble("gastado"));
                ranking.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ranking;
    }
}