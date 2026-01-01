package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.ComisionEspecial;
import com.vacas.model.ComisionGlobal;
import com.vacas.utils.DatabaseConnection;

public class ComisionDAO {
    
    public ComisionGlobal obtenerComisionGlobalActual() {
        String sql = "SELECT id, porcentaje, fecha_creacion FROM comision_global " +
                    "ORDER BY fecha_creacion DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                ComisionGlobal comision = new ComisionGlobal();
                comision.setId(rs.getInt("id"));
                comision.setPorcentaje(rs.getDouble("porcentaje"));
                comision.setFechaCreacion(rs.getDate("fecha_creacion"));
                return comision;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean crearComisionGlobal(ComisionGlobal comision) {
        String sql = "INSERT INTO comision_global (porcentaje, fecha_creacion) VALUES (?, CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, comision.getPorcentaje());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public ComisionEspecial obtenerComisionEspecial(int empresaId) {
        String sql = "SELECT id, empresa_id, porcentaje, fecha_creacion " +
                    "FROM comision_especial WHERE empresa_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, empresaId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                ComisionEspecial comision = new ComisionEspecial();
                comision.setId(rs.getInt("id"));
                comision.setEmpresaId(rs.getInt("empresa_id"));
                comision.setPorcentaje(rs.getDouble("porcentaje"));
                comision.setFechaCreacion(rs.getDate("fecha_creacion"));
                return comision;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean crearComisionEspecial(ComisionEspecial comision) {
        String sql = "INSERT INTO comision_especial (empresa_id, porcentaje, fecha_creacion) " +
                    "VALUES (?, ?, CURDATE()) " +
                    "ON DUPLICATE KEY UPDATE porcentaje = ?, fecha_creacion = CURDATE()";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, comision.getEmpresaId());
            stmt.setDouble(2, comision.getPorcentaje());
            stmt.setDouble(3, comision.getPorcentaje());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean eliminarComisionEspecial(int empresaId) {
        String sql = "DELETE FROM comision_especial WHERE empresa_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, empresaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<ComisionEspecial> obtenerTodasComisionesEspeciales() {
        List<ComisionEspecial> comisiones = new ArrayList<>();
        String sql = "SELECT ce.id, ce.empresa_id, ce.porcentaje, ce.fecha_creacion, " +
                    "e.nombre as empresa_nombre " +
                    "FROM comision_especial ce " +
                    "JOIN empresa e ON ce.empresa_id = e.id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                ComisionEspecial comision = new ComisionEspecial();
                comision.setId(rs.getInt("id"));
                comision.setEmpresaId(rs.getInt("empresa_id"));
                comision.setPorcentaje(rs.getDouble("porcentaje"));
                comision.setFechaCreacion(rs.getDate("fecha_creacion"));
                comisiones.add(comision);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comisiones;
    }
}