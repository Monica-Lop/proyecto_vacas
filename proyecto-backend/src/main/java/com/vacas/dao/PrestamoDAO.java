package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.Prestamo;
import com.vacas.utils.DatabaseConnection;

public class PrestamoDAO {
    
    public boolean instalar(Prestamo prestamo) {
        String sql = "INSERT INTO prestamo (usuario_id, videojuego_id, estado, fecha_instalacion) " +
                    "VALUES (?, ?, 'INSTALADO', CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, prestamo.getUsuarioId());
            stmt.setInt(2, prestamo.getVideojuegoId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean desinstalar(int usuarioId, int videojuegoId) {
        String sql = "UPDATE prestamo SET estado = 'NO_INSTALADO', fecha_desinstalacion = CURDATE() " +
                    "WHERE usuario_id = ? AND videojuego_id = ? AND estado = 'INSTALADO'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, videojuegoId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Prestamo obtenerInstaladoActualmente(int usuarioId) {
        String sql = "SELECT id, usuario_id, videojuego_id, estado, fecha_instalacion, fecha_desinstalacion " +
                    "FROM prestamo WHERE usuario_id = ? AND estado = 'INSTALADO'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setId(rs.getInt("id"));
                prestamo.setUsuarioId(rs.getInt("usuario_id"));
                prestamo.setVideojuegoId(rs.getInt("videojuego_id"));
                prestamo.setEstado(rs.getString("estado"));
                prestamo.setFechaInstalacion(rs.getDate("fecha_instalacion"));
                prestamo.setFechaDesinstalacion(rs.getDate("fecha_desinstalacion"));
                return prestamo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Prestamo> obtenerHistorial(int usuarioId) {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = "SELECT id, usuario_id, videojuego_id, estado, fecha_instalacion, fecha_desinstalacion " +
                    "FROM prestamo WHERE usuario_id = ? ORDER BY fecha_instalacion DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Prestamo prestamo = new Prestamo();
                prestamo.setId(rs.getInt("id"));
                prestamo.setUsuarioId(rs.getInt("usuario_id"));
                prestamo.setVideojuegoId(rs.getInt("videojuego_id"));
                prestamo.setEstado(rs.getString("estado"));
                prestamo.setFechaInstalacion(rs.getDate("fecha_instalacion"));
                prestamo.setFechaDesinstalacion(rs.getDate("fecha_desinstalacion"));
                prestamos.add(prestamo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestamos;
    }
}