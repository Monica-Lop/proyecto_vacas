package com.vacas.dao;

import com.vacas.model.Calificacion;
import com.vacas.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CalificacionDAO {
    
    public boolean crear(Calificacion calificacion) {
        String sql = "INSERT INTO calificacion (usuario_id, videojuego_id, estrellas, fecha_calificacion) " +
                    "VALUES (?, ?, ?, CURDATE()) " +
                    "ON DUPLICATE KEY UPDATE estrellas = ?, fecha_calificacion = CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, calificacion.getUsuarioId());
            stmt.setInt(2, calificacion.getVideojuegoId());
            stmt.setInt(3, calificacion.getEstrellas());
            stmt.setInt(4, calificacion.getEstrellas());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public double obtenerPromedio(int videojuegoId) {
        String sql = "SELECT AVG(estrellas) as promedio FROM calificacion WHERE videojuego_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, videojuegoId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("promedio");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
    public List<Calificacion> obtenerPorVideojuego(int videojuegoId) {
        List<Calificacion> calificaciones = new ArrayList<>();
        String sql = "SELECT c.id, c.usuario_id, c.videojuego_id, c.estrellas, " +
                    "c.fecha_calificacion, u.nickname " +
                    "FROM calificacion c " +
                    "JOIN usuario u ON c.usuario_id = u.id " +
                    "WHERE c.videojuego_id = ? " +
                    "ORDER BY c.fecha_calificacion DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, videojuegoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Calificacion calificacion = new Calificacion();
                calificacion.setId(rs.getInt("id"));
                calificacion.setUsuarioId(rs.getInt("usuario_id"));
                calificacion.setVideojuegoId(rs.getInt("videojuego_id"));
                calificacion.setEstrellas(rs.getInt("estrellas"));
                calificacion.setFechaCalificacion(rs.getDate("fecha_calificacion"));
                calificaciones.add(calificacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return calificaciones;
    }
    
    public boolean eliminar(int usuarioId, int videojuegoId) {
        String sql = "DELETE FROM calificacion WHERE usuario_id = ? AND videojuego_id = ?";
        
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
}