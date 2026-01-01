package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.Transaccion;
import com.vacas.utils.DatabaseConnection;

public class TransaccionDAO {
    
    public boolean crear(Transaccion transaccion) {
        String sql = "INSERT INTO transaccion (usuario_id, videojuego_id, fecha_compra, " +
                    "precio_pagado, monto_comision, tipo_comision, fecha_registro) " +
                    "VALUES (?, ?, ?, ?, ?, ?, CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, transaccion.getUsuarioId());
            stmt.setInt(2, transaccion.getVideojuegoId());
            stmt.setDate(3, new java.sql.Date(transaccion.getFechaCompra().getTime()));
            stmt.setDouble(4, transaccion.getPrecioPagado());
            stmt.setDouble(5, transaccion.getMontoComision());
            stmt.setString(6, transaccion.getTipoComision());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Transaccion> obtenerPorUsuario(int usuarioId) {
        List<Transaccion> transacciones = new ArrayList<>();
        String sql = "SELECT id, usuario_id, videojuego_id, fecha_compra, precio_pagado, " +
                    "monto_comision, tipo_comision, fecha_registro " +
                    "FROM transaccion WHERE usuario_id = ? ORDER BY fecha_compra DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Transaccion transaccion = new Transaccion();
                transaccion.setId(rs.getInt("id"));
                transaccion.setUsuarioId(rs.getInt("usuario_id"));
                transaccion.setVideojuegoId(rs.getInt("videojuego_id"));
                transaccion.setFechaCompra(rs.getDate("fecha_compra"));
                transaccion.setPrecioPagado(rs.getDouble("precio_pagado"));
                transaccion.setMontoComision(rs.getDouble("monto_comision"));
                transaccion.setTipoComision(rs.getString("tipo_comision"));
                transaccion.setFechaRegistro(rs.getDate("fecha_registro"));
                transacciones.add(transaccion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transacciones;
    }
    
    public double obtenerTotalComisiones() {
        String sql = "SELECT SUM(monto_comision) as total FROM transaccion";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}