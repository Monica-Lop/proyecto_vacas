package com.vacas.service;

import org.mindrot.jbcrypt.BCrypt;

import com.vacas.dao.UsuarioDAO;
import com.vacas.model.Usuario;

public class AuthService {
    private UsuarioDAO usuarioDAO;
    
    public AuthService() {
        this.usuarioDAO = new UsuarioDAO();
    }
    
    public Usuario login(String correo, String password) {
        return usuarioDAO.login(correo, password);
    }
    
    public boolean registrar(Usuario usuario, String password) {
        if (usuarioDAO.existeCorreo(usuario.getCorreo())) {
            return false;
        }
        
        if (usuarioDAO.existeNickname(usuario.getNickname())) {
            return false;
        }
        
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        usuario.setPassword(hashedPassword);
        
        return usuarioDAO.registrar(usuario);
    }
    
    public boolean recargarSaldo(int usuarioId, double monto) {
        if (monto <= 0) return false;
        
        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        if (usuario == null) return false;
        
        double nuevoSaldo = usuario.getSaldoCartera() + monto;
        return usuarioDAO.actualizarSaldo(usuarioId, nuevoSaldo);
    }
    
    public Usuario obtenerUsuarioPorId(int id) {
        // Este método sería implementado en UsuarioDAO
        // Por ahora retornamos null
        return null;
    }
}