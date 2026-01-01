package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.Empresa;
import com.vacas.utils.DatabaseConnection;

public class EmpresaDAO {
    
    // Método para crear una nueva empresa
    public boolean crear(Empresa empresa) {
        String sql = "INSERT INTO empresa (nombre, descripcion, telefono, politica_comentarios, fecha_creacion) " +
                    "VALUES (?, ?, ?, ?, CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, empresa.getNombre());
            stmt.setString(2, empresa.getDescripcion() != null ? empresa.getDescripcion() : "");
            stmt.setString(3, empresa.getTelefono() != null ? empresa.getTelefono() : "");
            stmt.setString(4, empresa.getPoliticaComentarios() != null ? empresa.getPoliticaComentarios() : "HABILITADOS");
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        empresa.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error creando empresa: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Método para obtener todas las empresas
    public List<Empresa> obtenerTodas() {
        List<Empresa> empresas = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, telefono, politica_comentarios, fecha_creacion " +
                    "FROM empresa ORDER BY nombre";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                empresas.add(mapearEmpresa(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo empresas: " + e.getMessage());
            e.printStackTrace();
        }
        return empresas;
    }
    
    // Método para obtener una empresa por ID
    public Empresa obtenerPorId(int id) {
        String sql = "SELECT id, nombre, descripcion, telefono, politica_comentarios, fecha_creacion " +
                    "FROM empresa WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapearEmpresa(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo empresa por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // Método para actualizar una empresa
    public boolean actualizar(Empresa empresa) {
        String sql = "UPDATE empresa SET nombre = ?, descripcion = ?, telefono = ?, " +
                    "politica_comentarios = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, empresa.getNombre());
            stmt.setString(2, empresa.getDescripcion() != null ? empresa.getDescripcion() : "");
            stmt.setString(3, empresa.getTelefono() != null ? empresa.getTelefono() : "");
            stmt.setString(4, empresa.getPoliticaComentarios() != null ? empresa.getPoliticaComentarios() : "HABILITADOS");
            stmt.setInt(5, empresa.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando empresa: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Método para eliminar una empresa (solo si no tiene videojuegos asociados)
    public boolean eliminar(int id) {
        // Primero verificar si tiene videojuegos asociados
        String verificarSql = "SELECT COUNT(*) as count FROM videojuego WHERE empresa_id = ?";
        String eliminarSql = "DELETE FROM empresa WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar si hay videojuegos asociados
            try (PreparedStatement verificarStmt = conn.prepareStatement(verificarSql)) {
                verificarStmt.setInt(1, id);
                ResultSet rs = verificarStmt.executeQuery();
                if (rs.next() && rs.getInt("count") > 0) {
                    System.err.println("No se puede eliminar empresa con videojuegos asociados");
                    return false;
                }
            }
            
            // Eliminar la empresa
            try (PreparedStatement eliminarStmt = conn.prepareStatement(eliminarSql)) {
                eliminarStmt.setInt(1, id);
                return eliminarStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error eliminando empresa: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Método para verificar si existe una empresa con el mismo nombre
    public boolean existeNombre(String nombre) {
        String sql = "SELECT id FROM empresa WHERE nombre = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error verificando nombre de empresa: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Método auxiliar para mapear ResultSet a Empresa
    private Empresa mapearEmpresa(ResultSet rs) throws SQLException {
        Empresa empresa = new Empresa();
        empresa.setId(rs.getInt("id"));
        empresa.setNombre(rs.getString("nombre"));
        empresa.setDescripcion(rs.getString("descripcion"));
        empresa.setTelefono(rs.getString("telefono"));
        empresa.setPoliticaComentarios(rs.getString("politica_comentarios"));
        empresa.setFechaCreacion(rs.getDate("fecha_creacion"));
        return empresa;
    }
}