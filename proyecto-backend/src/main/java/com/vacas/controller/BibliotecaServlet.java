package com.vacas.controller;

import com.google.gson.Gson;
import com.vacas.model.Biblioteca;
import com.vacas.service.BibliotecaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/biblioteca/*")
public class BibliotecaServlet extends HttpServlet {
    
    private BibliotecaService bibliotecaService = new BibliotecaService();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"No autenticado\"}");
            return;
        }
        
        int usuarioId = (int) session.getAttribute("usuarioId");
        
        try {
            List<Biblioteca> biblioteca = bibliotecaService.obtenerBibliotecaUsuario(usuarioId);
            out.print(gson.toJson(biblioteca));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error interno del servidor\"}");
            e.printStackTrace();
        }
        out.flush();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"No autenticado\"}");
            return;
        }
        
        int usuarioId = (int) session.getAttribute("usuarioId");
        
        try {
            String videojuegoIdStr = request.getParameter("videojuegoId");
            
            if (videojuegoIdStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"ID de videojuego requerido\"}");
                return;
            }
            
            int videojuegoId = Integer.parseInt(videojuegoIdStr);
            
            // Verificar si ya tiene el juego
            if (bibliotecaService.tieneJuego(usuarioId, videojuegoId)) {
                out.print("{\"success\": false, \"message\": \"Ya tienes este juego en tu biblioteca\"}");
                return;
            }
            
            Biblioteca biblioteca = new Biblioteca();
            biblioteca.setUsuarioId(usuarioId);
            biblioteca.setVideojuegoId(videojuegoId);
            
            boolean agregado = bibliotecaService.agregarJuego(biblioteca);
            
            if (agregado) {
                out.print("{\"success\": true, \"message\": \"Juego agregado a tu biblioteca\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Error al agregar juego\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"ID inválido\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Error interno del servidor\"}");
            e.printStackTrace();
        }
        out.flush();
    }
}