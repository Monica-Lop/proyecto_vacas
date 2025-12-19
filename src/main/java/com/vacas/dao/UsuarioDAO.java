package com.vacas.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import com.vacas.model.Usuario;
import com.vacas.util.ConexionBD;

public class UsuarioDAO {
    
    // BUSCAR usuario por correo 
    public Usuario buscarPorCorreo(String correo) {
        Usuario usuario = null;
        String sql = "SELECT * FROM usuario WHERE correo = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setPassword(rs.getString("password"));
                usuario.setNickname(rs.getString("nickname"));
                usuario.setTipo(rs.getString("tipo"));
                usuario.setActivo(rs.getBoolean("activo"));
                usuario.setTelefono(rs.getString("telefono"));
                usuario.setPais(rs.getString("pais"));

            }
            rs.close();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }
    
    // 2. CREAR nuevo usuario (registro)
    public boolean crear(Usuario usuario) {
        String sql = "INSERT INTO usuario (correo, password, nickname, fecha_nacimiento, tipo, activo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, usuario.getCorreo());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getNickname());
            ps.setString(4, usuario.getFechaNacimiento());
            ps.setString(5, usuario.getTipo());
            ps.setBoolean(6, usuario.isActivo());
            
            int filas = ps.executeUpdate();
            return filas > 0; // true si se insertó
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 3. LISTAR todos los usuarios (para admin)
    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario WHERE activo = true";
        
        try (Connection conn = ConexionBD.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setCorreo(rs.getString("correo"));
                u.setNickname(rs.getString("nickname"));
                u.setTipo(rs.getString("tipo"));
                usuarios.add(u);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

       // REGISTRAR USUARIO COMÚN
    public boolean registrarUsuarioComun(Usuario usuario) {
        String sql = "INSERT INTO usuario (correo, password, nickname, fecha_nacimiento, " +
                     "telefono, pais, tipo, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, usuario.getCorreo());
            ps.setString(2, usuario.getPassword()); // Texto plano temporalmente
            ps.setString(3, usuario.getNickname());
            ps.setDate(4, new java.sql.Date(usuario.getFechaNacimiento().getTime()));
            ps.setString(5, usuario.getTelefono());
            ps.setString(6, usuario.getPais());
            ps.setString(7, "USUARIO"); // Tipo fijo para usuario común
            ps.setBoolean(8, true);
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
                rs.close();
                System.out.println("Usuario común registrado: " + usuario.getCorreo());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println(" Error registrando usuario común: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // REGISTRAR USUARIO EMPRESA (con empresa_id)
    public boolean registrarUsuarioEmpresa(Usuario usuario, int empresaId) {
        String sql = "INSERT INTO usuario (correo, password, nickname, fecha_nacimiento, " +
                     "telefono, pais, tipo, activo, empresa_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, usuario.getCorreo());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getNickname());
            ps.setDate(4, new java.sql.Date(usuario.getFechaNacimiento().getTime()));
            ps.setString(5, usuario.getTelefono());
            ps.setString(6, usuario.getPais());
            ps.setString(7, "EMPRESA");
            ps.setBoolean(8, true);
            ps.setInt(9, empresaId);
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
                rs.close();
                System.out.println("Usuario empresa registrado: " + usuario.getCorreo() + 
                                  " para empresa ID: " + empresaId);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println(" Error registrando usuario empresa: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // REGISTRAR ADMINISTRADOR (solo desde código interno o con clave)
    public boolean registrarAdministrador(Usuario usuario) {
        String sql = "INSERT INTO usuario (correo, password, nickname, fecha_nacimiento, " +
                     "telefono, pais, tipo, activo) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, usuario.getCorreo());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getNickname());
            ps.setDate(4, new java.sql.Date(usuario.getFechaNacimiento().getTime()));
            ps.setString(5, usuario.getTelefono());
            ps.setString(6, usuario.getPais());
            ps.setString(7, "ADMIN");
            ps.setBoolean(8, true);
            
            int filas = ps.executeUpdate();
            
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
                rs.close();
                System.out.println(" Administrador registrado: " + usuario.getCorreo());
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("Error registrando administrador: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // VERIFICAR SI CORREO EXISTE
    public boolean existeCorreo(String correo) {
        String sql = "SELECT COUNT(*) as total FROM usuario WHERE correo = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error verificando correo: " + e.getMessage());
        }
        return false;
    }

}