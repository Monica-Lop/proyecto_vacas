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
    
    public boolean crear(Empresa empresa) {
        String sql = "INSERT INTO empresa (nombre, descripcion, telefono, fecha_creacion) " +
                    "VALUES (?, ?, ?, CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, empresa.getNombre());
            stmt.setString(2, empresa.getDescripcion());
            stmt.setString(3, empresa.getTelefono());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Empresa> obtenerTodas() {
        List<Empresa> empresas = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion, telefono, politica_comentarios, " +
                    "fecha_creacion FROM empresa";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Empresa empresa = new Empresa();
                empresa.setId(rs.getInt("id"));
                empresa.setNombre(rs.getString("nombre"));
                empresa.setDescripcion(rs.getString("descripcion"));
                empresa.setTelefono(rs.getString("telefono"));
                empresa.setPoliticaComentarios(rs.getString("politica_comentarios"));
                empresa.setFechaCreacion(rs.getDate("fecha_creacion"));
                empresas.add(empresa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empresas;
    }
    
    public Empresa obtenerPorId(int id) {
        String sql = "SELECT id, nombre, descripcion, telefono, politica_comentarios, " +
                    "fecha_creacion FROM empresa WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Empresa empresa = new Empresa();
                empresa.setId(rs.getInt("id"));
                empresa.setNombre(rs.getString("nombre"));
                empresa.setDescripcion(rs.getString("descripcion"));
                empresa.setTelefono(rs.getString("telefono"));
                empresa.setPoliticaComentarios(rs.getString("politica_comentarios"));
                empresa.setFechaCreacion(rs.getDate("fecha_creacion"));
                return empresa;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean existeNombre(String nombre) {
        String sql = "SELECT id FROM empresa WHERE nombre = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}