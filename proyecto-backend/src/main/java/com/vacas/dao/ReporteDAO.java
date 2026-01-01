package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vacas.utils.DatabaseConnection;

public class ReporteDAO {
    
    public Map<String, Object> obtenerEstadisticasGlobales(String fechaInicio, String fechaFin) throws SQLException {
        Map<String, Object> estadisticas = new HashMap<>();
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT ");
        sql.append("  COUNT(DISTINCT u.id) as total_usuarios, ");
        sql.append("  COUNT(DISTINCT e.id) as total_empresas, ");
        sql.append("  COUNT(DISTINCT v.id) as total_videojuegos, ");
        sql.append("  COUNT(DISTINCT t.id) as total_ventas, ");
        sql.append("  SUM(t.precio_pagado) as ingresos_totales, ");
        sql.append("  SUM(t.monto_comision) as comisiones_totales ");
        sql.append("FROM usuario u ");
        sql.append("CROSS JOIN empresa e ");
        sql.append("CROSS JOIN videojuego v ");
        sql.append("LEFT JOIN transaccion t ON 1=1 ");
        
        if (fechaInicio != null || fechaFin != null) {
            sql.append("WHERE 1=1 ");
            if (fechaInicio != null) {
                sql.append("AND t.fecha_compra >= ? ");
            }
            if (fechaFin != null) {
                sql.append("AND t.fecha_compra <= ? ");
            }
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (fechaInicio != null) {
                stmt.setString(paramIndex++, fechaInicio);
            }
            if (fechaFin != null) {
                stmt.setString(paramIndex++, fechaFin);
            }
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                estadisticas.put("totalUsuarios", rs.getInt("total_usuarios"));
                estadisticas.put("totalEmpresas", rs.getInt("total_empresas"));
                estadisticas.put("totalVideojuegos", rs.getInt("total_videojuegos"));
                estadisticas.put("totalVentas", rs.getInt("total_ventas"));
                estadisticas.put("ingresosTotales", rs.getDouble("ingresos_totales"));
                estadisticas.put("comisionesTotales", rs.getDouble("comisiones_totales"));
            }
        }
        return estadisticas;
    }
    
    public List<Map<String, Object>> obtenerVentasPorMes(int anio) throws SQLException {
        List<Map<String, Object>> ventasPorMes = new ArrayList<>();
        
        String sql = "SELECT MONTH(fecha_compra) as mes, " +
                    "  COUNT(*) as total_ventas, " +
                    "  SUM(precio_pagado) as ingresos, " +
                    "  SUM(monto_comision) as comisiones " +
                    "FROM transaccion " +
                    "WHERE YEAR(fecha_compra) = ? " +
                    "GROUP BY MONTH(fecha_compra) " +
                    "ORDER BY mes";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, anio);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> mes = new HashMap<>();
                mes.put("mes", rs.getInt("mes"));
                mes.put("totalVentas", rs.getInt("total_ventas"));
                mes.put("ingresos", rs.getDouble("ingresos"));
                mes.put("comisiones", rs.getDouble("comisiones"));
                ventasPorMes.add(mes);
            }
        }
        return ventasPorMes;
    }
    
    public List<Map<String, Object>> obtenerTopCategorias(int limite) throws SQLException {
        List<Map<String, Object>> topCategorias = new ArrayList<>();
        
        String sql = "SELECT c.nombre, " +
                    "  COUNT(DISTINCT v.id) as total_videojuegos, " +
                    "  COUNT(DISTINCT t.id) as total_ventas, " +
                    "  SUM(t.precio_pagado) as ingresos " +
                    "FROM categoria c " +
                    "LEFT JOIN videojuego_categoria vc ON c.id = vc.categoria_id " +
                    "LEFT JOIN videojuego v ON vc.videojuego_id = v.id " +
                    "LEFT JOIN transaccion t ON v.id = t.videojuego_id " +
                    "WHERE c.activa = TRUE " +
                    "GROUP BY c.id, c.nombre " +
                    "ORDER BY ingresos DESC " +
                    "LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, limite);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> categoria = new HashMap<>();
                categoria.put("nombre", rs.getString("nombre"));
                categoria.put("totalVideojuegos", rs.getInt("total_videojuegos"));
                categoria.put("totalVentas", rs.getInt("total_ventas"));
                categoria.put("ingresos", rs.getDouble("ingresos"));
                topCategorias.add(categoria);
            }
        }
        return topCategorias;
    }
}