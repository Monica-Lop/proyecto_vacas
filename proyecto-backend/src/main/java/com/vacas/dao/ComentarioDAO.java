package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.Comentario;
import com.vacas.util.ConexionBD;

public class ComentarioDAO {
    
    //  CREAR comentario
    public boolean crear(Comentario comentario) {
        String sql = "INSERT INTO comentario (usuario_id, videojuego_id, texto, calificacion, fecha, visible, comentario_padre_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, comentario.getUsuarioId());
            ps.setInt(2, comentario.getVideojuegoId());
            ps.setString(3, comentario.getTexto());
            ps.setInt(4, comentario.getCalificacion());
            ps.setTimestamp(5, new Timestamp(comentario.getFecha().getTime()));
            ps.setBoolean(6, comentario.isVisible());
            
            if (comentario.getComentarioPadreId() != null) {
                ps.setInt(7, comentario.getComentarioPadreId());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    comentario.setId(rs.getInt(1));
                }
                rs.close();
                
                // Actualizar promedio de calificaciones del videojuego
                actualizarPromedioCalificacion(comentario.getVideojuegoId());
            }
            
            System.out.println(" Comentario creado ID: " + comentario.getId());
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creando comentario: " + e.getMessage());
            return false;
        }
    }
    
    // ACTUALIZAR promedio de calificación del videojuego
    private void actualizarPromedioCalificacion(int videojuegoId) {
        String sql = "INSERT INTO videojuego_calificacion (videojuego_id, promedio, total_calificaciones) " +
                     "SELECT videojuego_id, AVG(calificacion), COUNT(*) " +
                     "FROM comentario WHERE videojuego_id = ? AND calificacion IS NOT NULL " +
                     "GROUP BY videojuego_id " +
                     "ON DUPLICATE KEY UPDATE promedio = VALUES(promedio), total_calificaciones = VALUES(total_calificaciones)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, videojuegoId);
            ps.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println(" Error actualizando promedio: " + e.getMessage());
        }
    }
    
    //  LISTAR comentarios por videojuego 
    public List<Comentario> listarPorVideojuego(int videojuegoId) {
        List<Comentario> comentarios = new ArrayList<>();
        String sql = "SELECT c.*, u.nickname as usuario_nickname FROM comentario c " +
                     "JOIN usuario u ON c.usuario_id = u.id " +
                     "WHERE c.videojuego_id = ? AND c.visible = true AND c.comentario_padre_id IS NULL " +
                     "ORDER BY c.fecha DESC";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, videojuegoId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Comentario c = new Comentario();
                c.setId(rs.getInt("id"));
                c.setUsuarioId(rs.getInt("usuario_id"));
                c.setVideojuegoId(videojuegoId);
                c.setTexto(rs.getString("texto"));
                c.setCalificacion(rs.getInt("calificacion"));
                c.setFecha(rs.getTimestamp("fecha"));
                c.setVisible(rs.getBoolean("visible"));
                comentarios.add(c);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println(" Error listando comentarios: " + e.getMessage());
        }
        return comentarios;
    }
    
    //  LISTAR respuestas de un comentario
    public List<Comentario> listarRespuestas(int comentarioPadreId) {
        List<Comentario> respuestas = new ArrayList<>();
        String sql = "SELECT c.*, u.nickname as usuario_nickname FROM comentario c " +
                     "JOIN usuario u ON c.usuario_id = u.id " +
                     "WHERE c.comentario_padre_id = ? AND c.visible = true " +
                     "ORDER BY c.fecha ASC";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, comentarioPadreId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Comentario c = new Comentario();
                c.setId(rs.getInt("id"));
                c.setUsuarioId(rs.getInt("usuario_id"));
                c.setVideojuegoId(rs.getInt("videojuego_id"));
                c.setTexto(rs.getString("texto"));
                c.setCalificacion(rs.getInt("calificacion"));
                c.setFecha(rs.getTimestamp("fecha"));
                c.setVisible(rs.getBoolean("visible"));
                c.setComentarioPadreId(comentarioPadreId);
                respuestas.add(c);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println(" Error listando respuestas: " + e.getMessage());
        }
        return respuestas;
    }
    
    // OBTENER calificación promedio de un videojuego
    public double obtenerCalificacionPromedio(int videojuegoId) {
        String sql = "SELECT promedio FROM videojuego_calificacion WHERE videojuego_id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, videojuegoId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("promedio");
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo calificación promedio: " + e.getMessage());
        }
        return 0.0;
    }
    
    //  OBTENER comentario por ID
    public Comentario obtenerPorId(int id) {
        Comentario comentario = null;
        String sql = "SELECT * FROM comentario WHERE id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                comentario = new Comentario();
                comentario.setId(rs.getInt("id"));
                comentario.setUsuarioId(rs.getInt("usuario_id"));
                comentario.setVideojuegoId(rs.getInt("videojuego_id"));
                comentario.setTexto(rs.getString("texto"));
                comentario.setCalificacion(rs.getInt("calificacion"));
                comentario.setFecha(rs.getTimestamp("fecha"));
                comentario.setVisible(rs.getBoolean("visible"));
                comentario.setComentarioPadreId(rs.getInt("comentario_padre_id"));
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo comentario: " + e.getMessage());
        }
        return comentario;
    }
    
    // ACTUALIZAR visibilidad 
    public boolean actualizarVisibilidad(int comentarioId, boolean visible) {
        String sql = "UPDATE comentario SET visible = ? WHERE id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setBoolean(1, visible);
            ps.setInt(2, comentarioId);
            
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println(" Error actualizando visibilidad: " + e.getMessage());
            return false;
        }
    }
    
    // ELIMINAR comentario 
    public boolean eliminar(int id) {
        return actualizarVisibilidad(id, false);
    }
}