package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.Videojuego;
import com.vacas.utils.DatabaseConnection;

public class VideojuegoDAO {
    
    public boolean crear(Videojuego videojuego) {
        String sql = "INSERT INTO videojuego (titulo, descripcion, precio, requisitos, " +
                    "edad_minima, fecha_lanzamiento, empresa_id, fecha_creacion) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, videojuego.getTitulo());
            stmt.setString(2, videojuego.getDescripcion());
            stmt.setDouble(3, videojuego.getPrecio());
            stmt.setString(4, videojuego.getRequisitos());
            stmt.setInt(5, videojuego.getEdadMinima());
            stmt.setDate(6, new java.sql.Date(videojuego.getFechaLanzamiento().getTime()));
            stmt.setInt(7, videojuego.getEmpresaId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Videojuego> obtenerTodos() {
        List<Videojuego> videojuegos = new ArrayList<>();
        String sql = "SELECT id, titulo, descripcion, precio, requisitos, edad_minima, " +
                    "disponible, fecha_lanzamiento, calificacion_promedio, " +
                    "comentarios_habilitados, empresa_id, fecha_creacion " +
                    "FROM videojuego WHERE disponible = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Videojuego videojuego = new Videojuego();
                videojuego.setId(rs.getInt("id"));
                videojuego.setTitulo(rs.getString("titulo"));
                videojuego.setDescripcion(rs.getString("descripcion"));
                videojuego.setPrecio(rs.getDouble("precio"));
                videojuego.setRequisitos(rs.getString("requisitos"));
                videojuego.setEdadMinima(rs.getInt("edad_minima"));
                videojuego.setDisponible(rs.getBoolean("disponible"));
                videojuego.setFechaLanzamiento(rs.getDate("fecha_lanzamiento"));
                videojuego.setCalificacionPromedio(rs.getDouble("calificacion_promedio"));
                videojuego.setComentariosHabilitados(rs.getBoolean("comentarios_habilitados"));
                videojuego.setEmpresaId(rs.getInt("empresa_id"));
                videojuego.setFechaCreacion(rs.getDate("fecha_creacion"));
                videojuegos.add(videojuego);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videojuegos;
    }
    
    public List<Videojuego> obtenerPorEmpresa(int empresaId) {
        List<Videojuego> videojuegos = new ArrayList<>();
        String sql = "SELECT id, titulo, descripcion, precio, requisitos, edad_minima, " +
                    "disponible, fecha_lanzamiento, calificacion_promedio, " +
                    "comentarios_habilitados, empresa_id, fecha_creacion " +
                    "FROM videojuego WHERE empresa_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, empresaId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Videojuego videojuego = new Videojuego();
                videojuego.setId(rs.getInt("id"));
                videojuego.setTitulo(rs.getString("titulo"));
                videojuego.setDescripcion(rs.getString("descripcion"));
                videojuego.setPrecio(rs.getDouble("precio"));
                videojuego.setRequisitos(rs.getString("requisitos"));
                videojuego.setEdadMinima(rs.getInt("edad_minima"));
                videojuego.setDisponible(rs.getBoolean("disponible"));
                videojuego.setFechaLanzamiento(rs.getDate("fecha_lanzamiento"));
                videojuego.setCalificacionPromedio(rs.getDouble("calificacion_promedio"));
                videojuego.setComentariosHabilitados(rs.getBoolean("comentarios_habilitados"));
                videojuego.setEmpresaId(rs.getInt("empresa_id"));
                videojuego.setFechaCreacion(rs.getDate("fecha_creacion"));
                videojuegos.add(videojuego);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videojuegos;
    }
    
    public Videojuego obtenerPorId(int id) {
        String sql = "SELECT id, titulo, descripcion, precio, requisitos, edad_minima, " +
                    "disponible, fecha_lanzamiento, calificacion_promedio, " +
                    "comentarios_habilitados, empresa_id, fecha_creacion " +
                    "FROM videojuego WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Videojuego videojuego = new Videojuego();
                videojuego.setId(rs.getInt("id"));
                videojuego.setTitulo(rs.getString("titulo"));
                videojuego.setDescripcion(rs.getString("descripcion"));
                videojuego.setPrecio(rs.getDouble("precio"));
                videojuego.setRequisitos(rs.getString("requisitos"));
                videojuego.setEdadMinima(rs.getInt("edad_minima"));
                videojuego.setDisponible(rs.getBoolean("disponible"));
                videojuego.setFechaLanzamiento(rs.getDate("fecha_lanzamiento"));
                videojuego.setCalificacionPromedio(rs.getDouble("calificacion_promedio"));
                videojuego.setComentariosHabilitados(rs.getBoolean("comentarios_habilitados"));
                videojuego.setEmpresaId(rs.getInt("empresa_id"));
                videojuego.setFechaCreacion(rs.getDate("fecha_creacion"));
                return videojuego;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean suspenderVenta(int id) {
        String sql = "UPDATE videojuego SET disponible = FALSE WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // MÉTODO NUEVO: Para usar en transacciones
public Videojuego obtenerPorId(int id, Connection conn) throws SQLException {
    String sql = "SELECT id, titulo, descripcion, precio, requisitos, edad_minima, " +
                "disponible, fecha_lanzamiento, calificacion_promedio, " +
                "comentarios_habilitados, empresa_id, fecha_creacion " +
                "FROM videojuego WHERE id = ?";
    
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            Videojuego videojuego = new Videojuego();
            videojuego.setId(rs.getInt("id"));
            videojuego.setTitulo(rs.getString("titulo"));
            videojuego.setDescripcion(rs.getString("descripcion"));
            videojuego.setPrecio(rs.getDouble("precio"));
            videojuego.setRequisitos(rs.getString("requisitos"));
            videojuego.setEdadMinima(rs.getInt("edad_minima"));
            videojuego.setDisponible(rs.getBoolean("disponible"));
            videojuego.setFechaLanzamiento(rs.getDate("fecha_lanzamiento"));
            videojuego.setCalificacionPromedio(rs.getDouble("calificacion_promedio"));
            videojuego.setComentariosHabilitados(rs.getBoolean("comentarios_habilitados"));
            videojuego.setEmpresaId(rs.getInt("empresa_id"));
            videojuego.setFechaCreacion(rs.getDate("fecha_creacion"));
            return videojuego;
        }
    }
    return null;
}
}