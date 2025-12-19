package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.Transaccion;
import com.vacas.util.ConexionBD;

public class TransaccionDAO {
    
    // CREAR transacción
    public boolean crear(Transaccion transaccion) {
        String sql = "INSERT INTO transaccion (usuario_id, videojuego_id, fecha_compra, " +
                     "precio_pagado, comision_aplicada, monto_comision, monto_empresa) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, transaccion.getUsuarioId());
            ps.setInt(2, transaccion.getVideojuegoId());
            ps.setTimestamp(3, new Timestamp(transaccion.getFechaCompra().getTime()));
            ps.setDouble(4, transaccion.getPrecioPagado());
            ps.setDouble(5, transaccion.getComisionAplicada());
            ps.setDouble(6, transaccion.getMontoComision());
            ps.setDouble(7, transaccion.getMontoEmpresa());
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    transaccion.setId(rs.getInt(1));
                }
                rs.close();
            }
            
            System.out.println(" Transacción creada ID: " + transaccion.getId());
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creando transacción: " + e.getMessage());
            return false;
        }
    }
    
    // LISTAR todas las transacciones
    public List<Transaccion> listarTodas() {
        List<Transaccion> transacciones = new ArrayList<>();
        String sql = "SELECT * FROM transaccion ORDER BY fecha_compra DESC";
        
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Transaccion t = new Transaccion();
                t.setId(rs.getInt("id"));
                t.setUsuarioId(rs.getInt("usuario_id"));
                t.setVideojuegoId(rs.getInt("videojuego_id"));
                t.setFechaCompra(rs.getTimestamp("fecha_compra"));
                t.setPrecioPagado(rs.getDouble("precio_pagado"));
                t.setComisionAplicada(rs.getDouble("comision_aplicada"));
                transacciones.add(t);
            }
            
            System.out.println("Listadas " + transacciones.size() + " transacciones");
            
        } catch (SQLException e) {
            System.err.println(" Error listando transacciones: " + e.getMessage());
        }
        return transacciones;
    }
    
    // LISTAR transacciones por usuario
    public List<Transaccion> listarPorUsuario(int usuarioId) {
        List<Transaccion> transacciones = new ArrayList<>();
        String sql = "SELECT t.*, v.titulo as videojuego_titulo FROM transaccion t " +
                     "JOIN videojuego v ON t.videojuego_id = v.id " +
                     "WHERE t.usuario_id = ? ORDER BY t.fecha_compra DESC";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Transaccion t = new Transaccion();
                t.setId(rs.getInt("id"));
                t.setUsuarioId(usuarioId);
                t.setVideojuegoId(rs.getInt("videojuego_id"));
                t.setFechaCompra(rs.getTimestamp("fecha_compra"));
                t.setPrecioPagado(rs.getDouble("precio_pagado"));
                t.setComisionAplicada(rs.getDouble("comision_aplicada"));
                transacciones.add(t);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println(" Error listando transacciones por usuario: " + e.getMessage());
        }
        return transacciones;
    }
    
    // OBTENER total de ventas por período
    public double obtenerTotalVentas(String fechaInicio, String fechaFin) {
        String sql = "SELECT SUM(precio_pagado) as total FROM transaccion " +
                     "WHERE fecha_compra BETWEEN ? AND ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println(" Error obteniendo total ventas: " + e.getMessage());
        }
        return 0.0;
    }
    
    // OBTENER comisiones por período
    public double obtenerTotalComisiones(String fechaInicio, String fechaFin) {
        String sql = "SELECT SUM(monto_comision) as total FROM transaccion " +
                     "WHERE fecha_compra BETWEEN ? AND ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, fechaInicio);
            ps.setString(2, fechaFin);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo total comisiones: " + e.getMessage());
        }
        return 0.0;
    }
}