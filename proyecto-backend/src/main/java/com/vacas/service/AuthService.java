package com.vacas.service;

import com.vacas.dao.UsuarioDAO;
import com.vacas.model.Usuario;

public class AuthService {
    private UsuarioDAO usuarioDao;
    
    public AuthService() {
        this.usuarioDao = new UsuarioDAO();
    }
    
    // Lógica de LOGIN
    public Usuario login(String correo, String password) {
        Usuario usuario = usuarioDao.buscarPorCorreo(correo);
        
        if (usuario == null) {
            System.out.println("Usuario no encontrado: " + correo);
            return null;
        }
        
        if (!usuario.isActivo()) {
            System.out.println("Usuario inactivo: " + correo);
            return null;
        }
        
        if (!password.equals(usuario.getPassword())) {
            System.out.println("Contraseña incorrecta para: " + correo);
            return null;
        }
        
        System.out.println("Login exitoso: " + usuario.getNickname());
        return usuario;
    }
    
    // Lógica de REGISTRO
    public boolean registrar(Usuario usuario) {
        if (usuario.getCorreo() == null || usuario.getCorreo().isEmpty()) {
            System.out.println("Correo requerido");
            return false;
        }
        
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            System.out.println("Password requerido");
            return false;
        }
        
        return usuarioDao.crear(usuario);
    }
}