package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.Videojuego;
import com.vacas.util.ConexionBD;

public class VideojuegoDAO {
    
    //  CREAR videojuego
    public boolean crear(Videojuego videojuego) {
        String sql = "INSERT INTO videojuego (titulo, descripcion, precio, edad_minima, " +
                     "requisitos, disponible, fecha_lanzamiento, empresa_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, videojuego.getTitulo());
            ps.setString(2, videojuego.getDescripcion());
            ps.setDouble(3, videojuego.getPrecio());
            ps.setInt(4, videojuego.getEdadMinima());
            ps.setString(5, videojuego.getRequisitos());
            ps.setBoolean(6, videojuego.isDisponible());
            
            if (videojuego.getFechaLanzamiento() != null) {
                ps.setDate(7, new java.sql.Date(videojuego.getFechaLanzamiento().getTime()));
            } else {
                ps.setNull(7, Types.DATE);
            }
            
            ps.setInt(8, videojuego.getEmpresaId());
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    videojuego.setId(rs.getInt(1));
                }
                rs.close();
            }
            
            System.out.println("Videojuego creado: " + videojuego.getTitulo());
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creando videojuego: " + e.getMessage());
            return false;
        }
    }
    
    //  LISTAR todos los videojuegos
    public List<Videojuego> listarTodos() {
        List<Videojuego> videojuegos = new ArrayList<>();
        String sql = "SELECT v.*, e.nombre as empresa_nombre " +
                     "FROM videojuego v JOIN empresa e ON v.empresa_id = e.id " +
                     "WHERE v.disponible = true ORDER BY v.titulo";
        
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Videojuego v = new Videojuego();
                v.setId(rs.getInt("id"));
                v.setTitulo(rs.getString("titulo"));
                v.setDescripcion(rs.getString("descripcion"));
                v.setPrecio(rs.getDouble("precio"));
                v.setEdadMinima(rs.getInt("edad_minima"));
                v.setRequisitos(rs.getString("requisitos"));
                v.setDisponible(rs.getBoolean("disponible"));
                v.setFechaLanzamiento(rs.getDate("fecha_lanzamiento"));
                v.setEmpresaId(rs.getInt("empresa_id"));
                
                videojuegos.add(v);
            }
            
            System.out.println(" Listados " + videojuegos.size() + " videojuegos");
            
        } catch (SQLException e) {
            System.err.println(" Error listando videojuegos: " + e.getMessage());
        }
        return videojuegos;
    }
    
    // BUSCAR por ID
    public Videojuego buscarPorId(int id) {
        Videojuego videojuego = null;
        String sql = "SELECT * FROM videojuego WHERE id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                videojuego = new Videojuego();
                videojuego.setId(rs.getInt("id"));
                videojuego.setTitulo(rs.getString("titulo"));
                videojuego.setDescripcion(rs.getString("descripcion"));
                videojuego.setPrecio(rs.getDouble("precio"));
                videojuego.setEdadMinima(rs.getInt("edad_minima"));
                videojuego.setRequisitos(rs.getString("requisitos"));
                videojuego.setDisponible(rs.getBoolean("disponible"));
                videojuego.setFechaLanzamiento(rs.getDate("fecha_lanzamiento"));
                videojuego.setEmpresaId(rs.getInt("empresa_id"));
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println(" Error buscando videojuego ID " + id + ": " + e.getMessage());
        }
        return videojuego;
    }
    
    //  LISTAR por empresa
    public List<Videojuego> listarPorEmpresa(int empresaId) {
        List<Videojuego> videojuegos = new ArrayList<>();
        String sql = "SELECT * FROM videojuego WHERE empresa_id = ? AND disponible = true ORDER BY titulo";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, empresaId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Videojuego v = new Videojuego();
                v.setId(rs.getInt("id"));
                v.setTitulo(rs.getString("titulo"));
                v.setDescripcion(rs.getString("descripcion"));
                v.setPrecio(rs.getDouble("precio"));
                v.setEdadMinima(rs.getInt("edad_minima"));
                v.setDisponible(rs.getBoolean("disponible"));
                v.setEmpresaId(empresaId);
                videojuegos.add(v);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error listando videojuegos por empresa: " + e.getMessage());
        }
        return videojuegos;
    }
    
    // ACTUALIZAR
    public boolean actualizar(Videojuego videojuego) {
        String sql = "UPDATE videojuego SET titulo = ?, descripcion = ?, precio = ?, " +
                     "edad_minima = ?, requisitos = ?, disponible = ?, " +
                     "fecha_lanzamiento = ? WHERE id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, videojuego.getTitulo());
            ps.setString(2, videojuego.getDescripcion());
            ps.setDouble(3, videojuego.getPrecio());
            ps.setInt(4, videojuego.getEdadMinima());
            ps.setString(5, videojuego.getRequisitos());
            ps.setBoolean(6, videojuego.isDisponible());
            
            if (videojuego.getFechaLanzamiento() != null) {
                ps.setDate(7, new java.sql.Date(videojuego.getFechaLanzamiento().getTime()));
            } else {
                ps.setNull(7, Types.DATE);
            }
            
            ps.setInt(8, videojuego.getId());
            
            int filas = ps.executeUpdate();
            System.out.println("Videojuego actualizado: " + videojuego.getTitulo());
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println(" Error actualizando videojuego: " + e.getMessage());
            return false;
        }
    }

    // ELIMINAR 
    public boolean eliminar(int id) {
        String sql = "UPDATE videojuego SET disponible = false WHERE id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            System.out.println(" Videojuego eliminado (soft) ID: " + id);
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println(" Error eliminando videojuego: " + e.getMessage());
            return false;
        }
    }
}