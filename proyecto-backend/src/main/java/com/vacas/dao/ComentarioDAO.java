package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.Comentario;
import com.vacas.utils.DatabaseConnection;

public class ComentarioDAO {
    
    public boolean crear(Comentario comentario) {
        String sql = "INSERT INTO comentario (usuario_id, videojuego_id, texto, " +
                    "fecha_comentario, visible, comentario_padre_id) " +
                    "VALUES (?, ?, ?, CURDATE(), ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, comentario.getUsuarioId());
            stmt.setInt(2, comentario.getVideojuegoId());
            stmt.setString(3, comentario.getTexto());
            stmt.setBoolean(4, comentario.isVisible());
            
            if (comentario.getComentarioPadreId() != null) {
                stmt.setInt(5, comentario.getComentarioPadreId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Comentario> obtenerPorVideojuego(int videojuegoId) {
        List<Comentario> comentarios = new ArrayList<>();
        String sql = "SELECT c.id, c.usuario_id, c.videojuego_id, c.texto, " +
                    "c.fecha_comentario, c.visible, c.comentario_padre_id, " +
                    "u.nickname " +
                    "FROM comentario c " +
                    "JOIN usuario u ON c.usuario_id = u.id " +
                    "WHERE c.videojuego_id = ? AND c.visible = TRUE " +
                    "ORDER BY c.fecha_comentario DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, videojuegoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Comentario comentario = new Comentario();
                comentario.setId(rs.getInt("id"));
                comentario.setUsuarioId(rs.getInt("usuario_id"));
                comentario.setVideojuegoId(rs.getInt("videojuego_id"));
                comentario.setTexto(rs.getString("texto"));
                comentario.setFechaComentario(rs.getDate("fecha_comentario"));
                comentario.setVisible(rs.getBoolean("visible"));
                comentario.setComentarioPadreId(rs.getInt("comentario_padre_id"));
                comentarios.add(comentario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comentarios;
    }
    
    public boolean ocultarComentario(int id) {
        String sql = "UPDATE comentario SET visible = FALSE WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean eliminar(int id) {
        String sql = "DELETE FROM comentario WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}