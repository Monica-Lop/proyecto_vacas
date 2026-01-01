package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.vacas.model.Usuario;
import com.vacas.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/registro")
public class RegistroServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");
        String nickname = request.getParameter("nickname");
        String fechaNacimientoStr = request.getParameter("fechaNacimiento");
        String telefono = request.getParameter("telefono");
        String pais = request.getParameter("pais");
        
        // Validaciones básicas
        if (correo == null || password == null || nickname == null || 
            fechaNacimientoStr == null || telefono == null || pais == null) {
            out.print("{\"success\": false, \"message\": \"Todos los campos son requeridos\"}");
            return;
        }
        
        // Convertir fecha
        Date fechaNacimiento;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            fechaNacimiento = sdf.parse(fechaNacimientoStr);
        } catch (ParseException e) {
            out.print("{\"success\": false, \"message\": \"Formato de fecha inválido\"}");
            return;
        }
        
        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setNickname(nickname);
        usuario.setFechaNacimiento(fechaNacimiento);
        usuario.setTelefono(telefono);
        usuario.setPais(pais);
        
        // Registrar
        AuthService authService = new AuthService();
        boolean registrado = authService.registrar(usuario, password);
        
        Gson gson = new Gson();
        String jsonResponse;
        
        if (registrado) {
            jsonResponse = gson.toJson(new RegistroResponse(true, "Usuario registrado exitosamente"));
        } else {
            jsonResponse = gson.toJson(new RegistroResponse(false, "El correo o nickname ya están en uso"));
        }
        
        out.print(jsonResponse);
        out.flush();
    }
    
    private class RegistroResponse {
        boolean success;
        String message;
        
        RegistroResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}