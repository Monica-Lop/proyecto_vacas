package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vacas.model.Cartera;
import com.vacas.util.ConexionBD;

public class CarteraDAO {
    
    // OBTENER saldo de usuario
    public Cartera obtenerPorUsuario(int usuarioId) {
        String sql = "SELECT * FROM cartera WHERE usuario_id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Cartera cartera = new Cartera();
                cartera.setUsuarioId(rs.getInt("usuario_id"));
                cartera.setSaldo(rs.getDouble("saldo"));
                return cartera;
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println(" Error obteniendo cartera: " + e.getMessage());
        }
        return null;
    }
    
    //  ACTUALIZAR saldo
    public boolean actualizarSaldo(int usuarioId, double nuevoSaldo) {
        String sql = "UPDATE cartera SET saldo = ? WHERE usuario_id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDouble(1, nuevoSaldo);
            ps.setInt(2, usuarioId);
            
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println(" Error actualizando cartera: " + e.getMessage());
            return false;
        }
    }
    
    // RECARGAR saldo
    public boolean recargarSaldo(int usuarioId, double monto) {
        Cartera cartera = obtenerPorUsuario(usuarioId);
        if (cartera == null) {
            return crearCartera(usuarioId, monto);
        }
        
        double nuevoSaldo = cartera.getSaldo() + monto;
        return actualizarSaldo(usuarioId, nuevoSaldo);
    }
    
    //  CREAR cartera
    public boolean crearCartera(int usuarioId, double saldoInicial) {
        String sql = "INSERT INTO cartera (usuario_id, saldo) VALUES (?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            ps.setDouble(2, saldoInicial);
            
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println(" Error creando cartera: " + e.getMessage());
            return false;
        }
    }
    
    // 5. DESCONTAR saldo 
    public boolean descontarSaldo(int usuarioId, double monto) {
        Cartera cartera = obtenerPorUsuario(usuarioId);
        if (cartera == null || cartera.getSaldo() < monto) {
            return false; // insuficiente saldo
        }
        
        double nuevoSaldo = cartera.getSaldo() - monto;
        return actualizarSaldo(usuarioId, nuevoSaldo);
    }
}