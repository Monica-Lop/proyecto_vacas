package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.vacas.model.Comentario;
import com.vacas.service.ComentarioService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/comentarios/*")
public class ComentarioServlet extends HttpServlet {
    
    private ComentarioService comentarioService = new ComentarioService();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/comentarios?videojuegoId=X
                String videojuegoIdStr = request.getParameter("videojuegoId");
                if (videojuegoIdStr != null) {
                    int videojuegoId = Integer.parseInt(videojuegoIdStr);
                    List<Comentario> comentarios = comentarioService.obtenerComentariosPorVideojuego(videojuegoId);
                    out.print(gson.toJson(comentarios));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Parámetro videojuegoId requerido\"}");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"ID inválido\"}");
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
            String texto = request.getParameter("texto");
            String comentarioPadreIdStr = request.getParameter("comentarioPadreId");
            
            if (videojuegoIdStr == null || texto == null || texto.trim().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Datos incompletos\"}");
                return;
            }
            
            int videojuegoId = Integer.parseInt(videojuegoIdStr);
            Integer comentarioPadreId = null;
            
            if (comentarioPadreIdStr != null && !comentarioPadreIdStr.isEmpty()) {
                comentarioPadreId = Integer.parseInt(comentarioPadreIdStr);
            }
            
            Comentario comentario = new Comentario();
            comentario.setUsuarioId(usuarioId);
            comentario.setVideojuegoId(videojuegoId);
            comentario.setTexto(texto);
            comentario.setVisible(true);
            comentario.setComentarioPadreId(comentarioPadreId);
            
            boolean creado = comentarioService.crearComentario(comentario);
            
            if (creado) {
                out.print("{\"success\": true, \"message\": \"Comentario publicado\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Error al publicar comentario\"}");
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
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        if (session == null || !"EMPRESA".equals(session.getAttribute("tipoUsuario"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"No autorizado\"}");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || !pathInfo.matches("/\\d+/ocultar")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Ruta inválida\"}");
            return;
        }
        
        try {
            int comentarioId = Integer.parseInt(pathInfo.split("/")[1]);
            boolean ocultado = comentarioService.ocultarComentario(comentarioId);
            
            if (ocultado) {
                out.print("{\"success\": true, \"message\": \"Comentario ocultado\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Comentario no encontrado\"}");
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