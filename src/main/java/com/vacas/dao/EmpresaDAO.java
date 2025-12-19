package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.Empresa;
import com.vacas.util.ConexionBD;

public class EmpresaDAO {

    // 1. Crear una nueva empresa
    public boolean crear(Empresa empresa) {
        String sql = "INSERT INTO empresa (nombre, descripcion, telefono, comision) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, empresa.getNombre());
            ps.setString(2, empresa.getDescripcion());
            ps.setString(3, empresa.getTelefono());
            ps.setDouble(4, empresa.getComision());
            
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al crear empresa: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 2. Listar todas las empresas
    public List<Empresa> listarTodas() {
        List<Empresa> empresas = new ArrayList<>();
        String sql = "SELECT * FROM empresa";
        
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Empresa emp = new Empresa();
                emp.setId(rs.getInt("id"));
                emp.setNombre(rs.getString("nombre"));
                emp.setDescripcion(rs.getString("descripcion"));
                emp.setTelefono(rs.getString("telefono"));
                emp.setComision(rs.getDouble("comision"));
                empresas.add(emp);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al listar empresas: " + e.getMessage());
            e.printStackTrace();
        }
        return empresas;
    }

    // 3. Buscar empresa por ID
    public Empresa buscarPorId(int id) {
        Empresa empresa = null;
        String sql = "SELECT * FROM empresa WHERE id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                empresa = new Empresa();
                empresa.setId(rs.getInt("id"));
                empresa.setNombre(rs.getString("nombre"));
                empresa.setDescripcion(rs.getString("descripcion"));
                empresa.setTelefono(rs.getString("telefono"));
                empresa.setComision(rs.getDouble("comision"));
            }
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error al buscar empresa por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return empresa;
    }

    // 4. Actualizar empresa
    public boolean actualizar(Empresa empresa) {
        String sql = "UPDATE empresa SET nombre = ?, descripcion = ?, telefono = ?, comision = ? WHERE id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, empresa.getNombre());
            ps.setString(2, empresa.getDescripcion());
            ps.setString(3, empresa.getTelefono());
            ps.setDouble(4, empresa.getComision());
            ps.setInt(5, empresa.getId());
            
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar empresa: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 5. Eliminar empresa (cambiar estado si es necesario, pero aquí eliminaremos físicamente)
    public boolean eliminar(int id) {
        String sql = "DELETE FROM empresa WHERE id = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.err.println("Error al eliminar empresa: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}