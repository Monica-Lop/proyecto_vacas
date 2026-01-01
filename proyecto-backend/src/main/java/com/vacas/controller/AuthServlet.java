package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.vacas.model.Usuario;
import com.vacas.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/login")
public class AuthServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");
        
        System.out.println("Login intent: " + correo);
        
        if (correo == null || password == null || correo.isEmpty() || password.isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Datos faltantes\"}");
            return;
        }
        
        AuthService authService = new AuthService();
        Usuario usuario = authService.login(correo, password);
        
        Gson gson = new Gson();
        String jsonResponse;
        
        if (usuario != null) {
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("tipoUsuario", usuario.getTipo());
            
            LoginResponse res = new LoginResponse(true, "Login exitoso", usuario);
            jsonResponse = gson.toJson(res);
        } else {
            LoginResponse res = new LoginResponse(false, "Credenciales incorrectas", null);
            jsonResponse = gson.toJson(res);
        }
        
        out.print(jsonResponse);
        out.flush();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        Gson gson = new Gson();
        
        if (session != null && session.getAttribute("usuario") != null) {
            Usuario usuario = (Usuario) session.getAttribute("usuario");
            AuthResponse res = new AuthResponse(true, "Autenticado", usuario);
            out.print(gson.toJson(res));
        } else {
            AuthResponse res = new AuthResponse(false, "No autenticado", null);
            out.print(gson.toJson(res));
        }
        out.flush();
    }
    
    @WebServlet("/api/logout")
    public static class LogoutServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) 
                throws ServletException, IOException {
            
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": true, \"message\": \"Sesión cerrada\"}");
            out.flush();
        }
    }
    
    private class LoginResponse {
        boolean success;
        String message;
        UsuarioData data;
        
        LoginResponse(boolean success, String message, Usuario usuario) {
            this.success = success;
            this.message = message;
            if (usuario != null) {
                this.data = new UsuarioData(usuario);
            }
        }
    }
    
    private class AuthResponse {
        boolean authenticated;
        String message;
        UsuarioData user;
        
        AuthResponse(boolean authenticated, String message, Usuario usuario) {
            this.authenticated = authenticated;
            this.message = message;
            if (usuario != null) {
                this.user = new UsuarioData(usuario);
            }
        }
    }
    
    private class UsuarioData {
        int id;
        String correo;
        String nickname;
        String tipo;
        Integer empresaId;
        double saldoCartera;
        
        UsuarioData(Usuario usuario) {
            this.id = usuario.getId();
            this.correo = usuario.getCorreo();
            this.nickname = usuario.getNickname();
            this.tipo = usuario.getTipo();
            this.empresaId = usuario.getEmpresaId();
            this.saldoCartera = usuario.getSaldoCartera();
        }
    }
}