package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.Categoria;
import com.vacas.util.ConexionBD;

public class CategoriaDAO {
    
    // CREAR categoría
    public boolean crear(Categoria categoria) {
        String sql = "INSERT INTO categoria (nombre, descripcion, activa) VALUES (?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setBoolean(3, categoria.isActiva());
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    categoria.setId(rs.getInt(1));
                }
                rs.close();
            }
            
            System.out.println("Categoría creada: " + categoria.getNombre());
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println(" Error creando categoría: " + e.getMessage());
            return false;
        }
    }
    
    // LISTAR todas las categorías activas
    public List<Categoria> listarTodas() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = "SELECT * FROM categoria WHERE activa = true ORDER BY nombre";
        
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Categoria cat = new Categoria();
                cat.setId(rs.getInt("id"));
                cat.setNombre(rs.getString("nombre"));
                cat.setDescripcion(rs.getString("descripcion"));
                cat.setActiva(rs.getBoolean("activa"));
                categorias.add(cat);
            }
            
            System.out.println("Listadas " + categorias.size() + " categorías");
            
        } catch (SQLException e) {
            System.err.println("Error listando categorías: " + e.getMessage());
        }
        return categorias;
    }
    
    // 3. BUSCAR por ID
    public Categoria buscarPorId(int id) {
        Categoria categoria = null;
        String sql = "SELECT * FROM categoria WHERE id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNombre(rs.getString("nombre"));
                categoria.setDescripcion(rs.getString("descripcion"));
                categoria.setActiva(rs.getBoolean("activa"));
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Error buscando categoría ID " + id + ": " + e.getMessage());
        }
        return categoria;
    }
    
    //  BUSCAR por nombre 
    public Categoria buscarPorNombre(String nombre) {
        Categoria categoria = null;
        String sql = "SELECT * FROM categoria WHERE nombre = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNombre(rs.getString("nombre"));
                categoria.setDescripcion(rs.getString("descripcion"));
                categoria.setActiva(rs.getBoolean("activa"));
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println(" Error buscando categoría por nombre: " + e.getMessage());
        }
        return categoria;
    }
    
    // 5. ACTUALIZAR
    public boolean actualizar(Categoria categoria) {
        String sql = "UPDATE categoria SET nombre = ?, descripcion = ?, activa = ? WHERE id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setBoolean(3, categoria.isActiva());
            ps.setInt(4, categoria.getId());
            
            int filas = ps.executeUpdate();
            System.out.println(" Categoría actualizada: " + categoria.getNombre());
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error actualizando categoría: " + e.getMessage());
            return false;
        }
    }
    
    // ELIMINAR 
    public boolean eliminar(int id) {
        String sql = "UPDATE categoria SET activa = false WHERE id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            System.out.println("Categoría eliminada (soft) ID: " + id);
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error eliminando categoría: " + e.getMessage());
            return false;
        }
    }
    
    // ASIGNAR categoría a videojuego
    public boolean asignarCategoriaAVideojuego(int videojuegoId, int categoriaId) {
        String sql = "INSERT INTO videojuego_categoria (videojuego_id, categoria_id) VALUES (?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, videojuegoId);
            ps.setInt(2, categoriaId);
            
            int filas = ps.executeUpdate();
            System.out.println(" Categoría " + categoriaId + " asignada a videojuego " + videojuegoId);
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error asignando categoría: " + e.getMessage());
            return false;
        }
    }

    
}