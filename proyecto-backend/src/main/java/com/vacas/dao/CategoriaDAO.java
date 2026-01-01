package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.Categoria;
import com.vacas.utils.DatabaseConnection;

public class CategoriaDAO {
    
    // Método para crear una nueva categoría
    public boolean crear(Categoria categoria) {
        String sql = "INSERT INTO categoria (nombre, activa) VALUES (?, TRUE)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, categoria.getNombre());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        categoria.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creando categoría: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Método para obtener todas las categorías activas
    public List<Categoria> obtenerTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT id, nombre, activa FROM categoria WHERE activa = TRUE ORDER BY nombre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo categorías: " + e.getMessage());
            e.printStackTrace();
        }
        return categorias;
    }
    
    // Método para obtener una categoría por ID
    public Categoria obtenerPorId(int id) {
        String sql = "SELECT id, nombre, activa FROM categoria WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearCategoria(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo categoría por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Método para buscar categoría por nombre
    public Categoria buscarPorNombre(String nombre) {
        String sql = "SELECT id, nombre, activa FROM categoria WHERE nombre = ? AND activa = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearCategoria(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error buscando categoría por nombre: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Método para actualizar una categoría
    public boolean actualizar(Categoria categoria) {
        String sql = "UPDATE categoria SET nombre = ?, activa = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, categoria.getNombre());
            stmt.setBoolean(2, categoria.isActiva());
            stmt.setInt(3, categoria.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando categoría: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Método para desactivar (eliminar lógicamente) una categoría
    public boolean eliminar(int id) {
        String sql = "UPDATE categoria SET activa = FALSE WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error eliminando categoría: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Método para asignar una categoría a un videojuego
    public boolean asignarCategoriaAVideojuego(int videojuegoId, int categoriaId) {
        // Verificar si ya existe la asignación
        if (existeAsignacion(videojuegoId, categoriaId)) {
            return true; // Ya está asignada
        }
        
        String sql = "INSERT INTO videojuego_categoria (videojuego_id, categoria_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, videojuegoId);
            stmt.setInt(2, categoriaId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error asignando categoría a videojuego: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Método auxiliar para verificar si ya existe una asignación
    private boolean existeAsignacion(int videojuegoId, int categoriaId) {
        String sql = "SELECT id FROM videojuego_categoria WHERE videojuego_id = ? AND categoria_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, videojuegoId);
            stmt.setInt(2, categoriaId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error verificando asignación: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Método para obtener categorías de un videojuego específico
    public List<Categoria> obtenerCategoriasPorVideojuego(int videojuegoId) {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT c.id, c.nombre, c.activa " +
                    "FROM categoria c " +
                    "INNER JOIN videojuego_categoria vc ON c.id = vc.categoria_id " +
                    "WHERE vc.videojuego_id = ? AND c.activa = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, videojuegoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo categorías por videojuego: " + e.getMessage());
            e.printStackTrace();
        }
        return categorias;
    }
    
    // Método auxiliar para mapear ResultSet a Categoria
    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setId(rs.getInt("id"));
        categoria.setNombre(rs.getString("nombre"));
        categoria.setActiva(rs.getBoolean("activa"));
        return categoria;
    }
}