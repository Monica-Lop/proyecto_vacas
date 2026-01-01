package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.vacas.model.Usuario;
import com.vacas.utils.DatabaseConnection;

public class UsuarioDAO {
    
    public Usuario login(String correo, String password) {
        String sql = "SELECT id, correo, password, nickname, tipo_usuario, empresa_id, " +
                    "saldo_cartera, fecha_nacimiento, telefono, pais, activo, " +
                    "biblioteca_publica, fecha_creacion " +
                    "FROM usuario WHERE correo = ? AND activo = TRUE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                
                // Verificar con BCrypt
                if (org.mindrot.jbcrypt.BCrypt.checkpw(password, hashedPassword)) {
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setNickname(rs.getString("nickname"));
                    usuario.setTipo(rs.getString("tipo_usuario"));
                    usuario.setEmpresaId(rs.getInt("empresa_id"));
                    usuario.setSaldoCartera(rs.getDouble("saldo_cartera"));
                    usuario.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                    usuario.setTelefono(rs.getString("telefono"));
                    usuario.setPais(rs.getString("pais"));
                    usuario.setActivo(rs.getBoolean("activo"));
                    usuario.setBibliotecaPublica(rs.getBoolean("biblioteca_publica"));
                    usuario.setFechaCreacion(rs.getDate("fecha_creacion"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean registrar(Usuario usuario) {
        String sql = "INSERT INTO usuario (correo, password, nickname, fecha_nacimiento, " +
                    "telefono, pais, tipo_usuario, fecha_creacion) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 'COMUN', CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getCorreo());
            stmt.setString(2, usuario.getPassword());
            stmt.setString(3, usuario.getNickname());
            stmt.setDate(4, new java.sql.Date(usuario.getFechaNacimiento().getTime()));
            stmt.setString(5, usuario.getTelefono());
            stmt.setString(6, usuario.getPais());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean existeCorreo(String correo) {
        String sql = "SELECT id FROM usuario WHERE correo = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean existeNickname(String nickname) {
        String sql = "SELECT id FROM usuario WHERE nickname = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nickname);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, correo, nickname, tipo_usuario, empresa_id, " +
                    "saldo_cartera, fecha_creacion, activo FROM usuario";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setNickname(rs.getString("nickname"));
                usuario.setTipo(rs.getString("tipo_usuario"));
                usuario.setEmpresaId(rs.getInt("empresa_id"));
                usuario.setSaldoCartera(rs.getDouble("saldo_cartera"));
                usuario.setFechaCreacion(rs.getDate("fecha_creacion"));
                usuario.setActivo(rs.getBoolean("activo"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }
    
    public boolean actualizarSaldo(int usuarioId, double nuevoSaldo) {
        String sql = "UPDATE usuario SET saldo_cartera = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, nuevoSaldo);
            stmt.setInt(2, usuarioId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}