package com.vacas.dao;

import com.vacas.model.GrupoFamiliar;
import com.vacas.model.MiembroGrupo;
import com.vacas.utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GrupoFamiliarDAO {
    
    public int crear(GrupoFamiliar grupo) {
        String sql = "INSERT INTO grupo_familiar (nombre, fecha_creacion, admin_id) " +
                    "VALUES (?, CURDATE(), ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, grupo.getNombre());
            stmt.setInt(2, grupo.getAdminId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public boolean agregarMiembro(MiembroGrupo miembro) {
        String sql = "INSERT INTO miembro_grupo (grupo_id, usuario_id, estado, fecha_union) " +
                    "VALUES (?, ?, ?, CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, miembro.getGrupoId());
            stmt.setInt(2, miembro.getUsuarioId());
            stmt.setString(3, miembro.getEstado());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean actualizarEstadoMiembro(int grupoId, int usuarioId, String estado) {
        String sql = "UPDATE miembro_grupo SET estado = ? WHERE grupo_id = ? AND usuario_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, estado);
            stmt.setInt(2, grupoId);
            stmt.setInt(3, usuarioId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<MiembroGrupo> obtenerMiembros(int grupoId) {
        List<MiembroGrupo> miembros = new ArrayList<>();
        String sql = "SELECT mg.id, mg.grupo_id, mg.usuario_id, mg.estado, mg.fecha_union, " +
                    "u.nickname " +
                    "FROM miembro_grupo mg " +
                    "JOIN usuario u ON mg.usuario_id = u.id " +
                    "WHERE mg.grupo_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, grupoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                MiembroGrupo miembro = new MiembroGrupo();
                miembro.setId(rs.getInt("id"));
                miembro.setGrupoId(rs.getInt("grupo_id"));
                miembro.setUsuarioId(rs.getInt("usuario_id"));
                miembro.setEstado(rs.getString("estado"));
                miembro.setFechaUnion(rs.getDate("fecha_union"));
                miembros.add(miembro);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return miembros;
    }
    
    public List<GrupoFamiliar> obtenerGruposPorUsuario(int usuarioId) {
        List<GrupoFamiliar> grupos = new ArrayList<>();
        String sql = "SELECT gf.id, gf.nombre, gf.fecha_creacion, gf.admin_id " +
                    "FROM grupo_familiar gf " +
                    "JOIN miembro_grupo mg ON gf.id = mg.grupo_id " +
                    "WHERE mg.usuario_id = ? AND mg.estado = 'ACTIVO'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                GrupoFamiliar grupo = new GrupoFamiliar();
                grupo.setId(rs.getInt("id"));
                grupo.setNombre(rs.getString("nombre"));
                grupo.setFechaCreacion(rs.getDate("fecha_creacion"));
                grupo.setAdminId(rs.getInt("admin_id"));
                grupos.add(grupo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return grupos;
    }
}