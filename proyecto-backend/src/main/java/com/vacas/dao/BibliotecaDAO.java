package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.Biblioteca;
import com.vacas.utils.DatabaseConnection;

public class BibliotecaDAO {
    
    public boolean agregar(Biblioteca biblioteca) {
        String sql = "INSERT INTO biblioteca (usuario_id, videojuego_id, fecha_agregado) " +
                    "VALUES (?, ?, CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, biblioteca.getUsuarioId());
            stmt.setInt(2, biblioteca.getVideojuegoId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Biblioteca> obtenerPorUsuario(int usuarioId) {
        List<Biblioteca> bibliotecas = new ArrayList<>();
        String sql = "SELECT b.id, b.usuario_id, b.videojuego_id, b.fecha_agregado, " +
                    "v.titulo, v.precio, v.calificacion_promedio " +
                    "FROM biblioteca b " +
                    "JOIN videojuego v ON b.videojuego_id = v.id " +
                    "WHERE b.usuario_id = ? " +
                    "ORDER BY b.fecha_agregado DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Biblioteca biblioteca = new Biblioteca();
                biblioteca.setId(rs.getInt("id"));
                biblioteca.setUsuarioId(rs.getInt("usuario_id"));
                biblioteca.setVideojuegoId(rs.getInt("videojuego_id"));
                biblioteca.setFechaAgregado(rs.getDate("fecha_agregado"));
                bibliotecas.add(biblioteca);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bibliotecas;
    }
    
    public boolean existe(int usuarioId, int videojuegoId) {
        String sql = "SELECT id FROM biblioteca WHERE usuario_id = ? AND videojuego_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            stmt.setInt(2, videojuegoId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
}

public boolean agregar(int usuarioId, int videojuegoId, Connection conn) throws SQLException {
    String sql = "INSERT INTO biblioteca (usuario_id, videojuego_id, fecha_agregado) VALUES (?, ?, CURDATE())";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, usuarioId);
        stmt.setInt(2, videojuegoId);
        return stmt.executeUpdate() > 0;
    }
}

public boolean existe(int usuarioId, int videojuegoId, Connection conn) throws SQLException {
    String sql = "SELECT id FROM biblioteca WHERE usuario_id = ? AND videojuego_id = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, usuarioId);
        stmt.setInt(2, videojuegoId);
        ResultSet rs = stmt.executeQuery();
        return rs.next();
    }
}
}