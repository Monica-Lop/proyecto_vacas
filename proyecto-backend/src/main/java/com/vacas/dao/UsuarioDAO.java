package com.vacas.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

import com.vacas.model.Usuario;
import com.vacas.utils.DatabaseConnection;

public class UsuarioDAO {
    
    // ==================== MÉTODOS DE AUTENTICACIÓN ====================
    
    /**
     * Autentica un usuario con correo y contraseña
     * @return Usuario autenticado o null si falla
     */
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
                
                // Verificar contraseña con BCrypt
                if (BCrypt.checkpw(password, hashedPassword)) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Registra un nuevo usuario (hashea la contraseña automáticamente)
     * @return true si el registro fue exitoso
     */
    public boolean registrar(Usuario usuario) {
        // Validar que no exista el correo
        if (existeCorreo(usuario.getCorreo())) {
            System.err.println("El correo ya está registrado: " + usuario.getCorreo());
            return false;
        }
        
        // Validar que no exista el nickname
        if (existeNickname(usuario.getNickname())) {
            System.err.println("El nickname ya está en uso: " + usuario.getNickname());
            return false;
        }
        
        // Hashear la contraseña antes de guardar
        String hashedPassword = BCrypt.hashpw(usuario.getPassword(), BCrypt.gensalt(12));
        
        String sql = "INSERT INTO usuario (correo, password, nickname, fecha_nacimiento, " +
                    "telefono, pais, tipo_usuario, saldo_cartera, fecha_creacion) " +
                    "VALUES (?, ?, ?, ?, ?, ?, 'COMUN', 0.00, CURDATE())";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, usuario.getCorreo());
            stmt.setString(2, hashedPassword); // Guarda el hash, no el texto plano
            stmt.setString(3, usuario.getNickname());
            stmt.setDate(4, new java.sql.Date(usuario.getFechaNacimiento().getTime()));
            stmt.setString(5, usuario.getTelefono());
            stmt.setString(6, usuario.getPais());
            
            int filasAfectadas = stmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getInt(1));
                        System.out.println("Usuario registrado exitosamente. ID: " + usuario.getId());
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error registrando usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // ==================== MÉTODOS DE CONSULTA ====================
    
    /**
     * Obtiene un usuario por su ID (sin transacción)
     */
    public Usuario obtenerPorId(int id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return obtenerPorId(id, conn);
        } catch (SQLException e) {
            System.err.println("Error obteniendo usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Obtiene un usuario por su ID (para uso en transacciones)
     * @param conn Conexión existente (no se cierra aquí)
     */
    public Usuario obtenerPorId(int id, Connection conn) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapearUsuario(rs);
            }
        }
        return null;
    }
    
    /**
     * Obtiene todos los usuarios (para administración)
     */
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
            System.err.println("Error obteniendo todos los usuarios: " + e.getMessage());
            e.printStackTrace();
        }
        return usuarios;
    }
    
    /**
     * Verifica si un correo ya está registrado
     */
    public boolean existeCorreo(String correo) {
        String sql = "SELECT id FROM usuario WHERE correo = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, correo);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error verificando correo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Verifica si un nickname ya está en uso
     */
    public boolean existeNickname(String nickname) {
        String sql = "SELECT id FROM usuario WHERE nickname = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nickname);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error verificando nickname: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // ==================== MÉTODOS DE SALDO/CARTERA ====================
    
    /**
     * Obtiene el saldo actual de un usuario
     */
    public double obtenerSaldo(int usuarioId) {
        String sql = "SELECT saldo_cartera FROM usuario WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("saldo_cartera");
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo saldo: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }
    
    /**
     * Ajusta el saldo sumando o restando (método seguro para transacciones concurrentes)
     * @param cambio Valor positivo para recargar, negativo para descontar
     * @return true si la operación fue exitosa
     */
    public boolean ajustarSaldo(int usuarioId, double cambio) {
        // Validación para descuentos: no permitir saldo negativo
        if (cambio < 0) {
            double saldoActual = obtenerSaldo(usuarioId);
            if (saldoActual + cambio < 0) {
                System.err.println("Saldo insuficiente. Usuario ID: " + usuarioId + 
                                 ", Saldo actual: " + saldoActual + 
                                 ", Intento de descuento: " + Math.abs(cambio));
                return false;
            }
        }
        
        String sql = "UPDATE usuario SET saldo_cartera = saldo_cartera + ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, cambio);
            stmt.setInt(2, usuarioId);
            
            boolean exito = stmt.executeUpdate() > 0;
            if (exito) {
                System.out.println("Saldo ajustado. Usuario ID: " + usuarioId + 
                                 ", Cambio: " + cambio + 
                                 ", Nuevo saldo: " + obtenerSaldo(usuarioId));
            }
            return exito;
        } catch (SQLException e) {
            System.err.println("Error ajustando saldo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Recarga saldo a un usuario (método específico para recargas)
     */
    public boolean recargarSaldo(int usuarioId, double monto) {
        if (monto <= 0) {
            System.err.println("Monto de recarga no válido: " + monto);
            return false;
        }
        return ajustarSaldo(usuarioId, monto);
    }
    
    /**
     * Descuenta saldo para una compra (método específico para compras)
     */
    public boolean descontarSaldo(int usuarioId, double monto) {
        if (monto <= 0) {
            System.err.println("Monto de descuento no válido: " + monto);
            return false;
        }
        return ajustarSaldo(usuarioId, -monto);
    }
    
    /**
     * Descuenta saldo dentro de una transacción (para TransaccionService)
     * @param conn Conexión existente de la transacción
     */
    public boolean descontarSaldo(int usuarioId, double monto, Connection conn) throws SQLException {
        String sql = "UPDATE usuario SET saldo_cartera = saldo_cartera - ? WHERE id = ? AND saldo_cartera >= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, monto);
            stmt.setInt(2, usuarioId);
            stmt.setDouble(3, monto); // Verifica que tenga saldo suficiente
            int filas = stmt.executeUpdate();
            return filas > 0;
        }
    }
    
    /**
     * Actualiza el saldo a un valor específico (método viejo - usar ajustarSaldo en su lugar)
     * @deprecated Usar {@link #ajustarSaldo(int, double)} en su lugar
     */
    @Deprecated
    public boolean actualizarSaldo(int usuarioId, double nuevoSaldo) {
        System.err.println("ADVERTENCIA: actualizarSaldo es inseguro. Usar ajustarSaldo en su lugar.");
        
        String sql = "UPDATE usuario SET saldo_cartera = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, nuevoSaldo);
            stmt.setInt(2, usuarioId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en actualizarSaldo: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    /**
     * Convierte un ResultSet en un objeto Usuario
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
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
    
    /**
     * Actualiza los datos básicos de un usuario
     */
    public boolean actualizarPerfil(Usuario usuario) {
        String sql = "UPDATE usuario SET nickname = ?, telefono = ?, pais = ?, " +
                    "biblioteca_publica = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getNickname());
            stmt.setString(2, usuario.getTelefono());
            stmt.setString(3, usuario.getPais());
            stmt.setBoolean(4, usuario.isBibliotecaPublica());
            stmt.setInt(5, usuario.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error actualizando perfil: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Activa o desactiva un usuario
     */
    public boolean cambiarEstado(int usuarioId, boolean activo) {
        String sql = "UPDATE usuario SET activo = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, activo);
            stmt.setInt(2, usuarioId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error cambiando estado de usuario: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}