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
        // 1. Buscar usuario
        Usuario usuario = usuarioDao.buscarPorCorreo(correo);
        
        // 2. Si no existe
        if (usuario == null) {
            System.out.println("Usuario no encontrado: " + correo);
            return null;
        }
        
        // 3. Si está inactivo
        if (!usuario.isActivo()) {
            System.out.println("Usuario inactivo: " + correo);
            return null;
        }
        
        // 4. Verificar contraseña (TEXTO PLANO por ahora)
        if (!password.equals(usuario.getPassword())) {
            System.out.println("Contraseña incorrecta para: " + correo);
            return null;
        }
        
        // 5. ÉXITO
        System.out.println("Login exitoso: " + usuario.getNickname());
        return usuario;
    }
    
    // Lógica de REGISTRO
    public boolean registrar(Usuario usuario) {
        // Validaciones simples
        if (usuario.getCorreo() == null || usuario.getCorreo().isEmpty()) {
            System.out.println("Correo requerido");
            return false;
        }
        
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            System.out.println("Password requerido");
            return false;
        }
        
        // Crear usuario
        return usuarioDao.crear(usuario);
    }
}