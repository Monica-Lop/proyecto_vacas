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

@WebServlet("/comentarios")
public class ComentarioServlet extends HttpServlet {
    
    private ComentarioService comentarioService;
    private Gson gson;
    
    @Override
    public void init() {
        this.comentarioService = new ComentarioService();
        this.gson = new Gson();
    }
    
    // POST: Crear comentario o calificación
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            int usuarioId = Integer.parseInt(request.getParameter("usuario_id"));
            int videojuegoId = Integer.parseInt(request.getParameter("videojuego_id"));
            String texto = request.getParameter("texto");
            int calificacion = Integer.parseInt(request.getParameter("calificacion"));
            
            //Comentario padre 
            String padreIdParam = request.getParameter("comentario_padre_id");
            Integer comentarioPadreId = null;
            if (padreIdParam != null && !padreIdParam.trim().isEmpty()) {
                comentarioPadreId = Integer.parseInt(padreIdParam);
            }
            
            Comentario comentario = comentarioService.crearComentario(usuarioId, videojuegoId, texto, calificacion, comentarioPadreId);
            
            if (comentario != null) {
                out.print("{\"success\": true, \"message\": \"Comentario publicado\", " +
                         "\"comentario_id\": " + comentario.getId() + "}");
            } else {
                response.setStatus(400);
                out.print("{\"error\": \"No se pudo publicar el comentario\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
    
    // GET: Obtener comentarios de un videojuego o respuestas
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String videojuegoIdParam = request.getParameter("videojuego_id");
        String comentarioPadreIdParam = request.getParameter("comentario_padre_id");
        
        try {
            if (videojuegoIdParam != null) {
                int videojuegoId = Integer.parseInt(videojuegoIdParam);
                List<Comentario> comentarios = comentarioService.obtenerComentariosVideojuego(videojuegoId);
                
                double promedio = comentarioService.obtenerCalificacionPromedio(videojuegoId);
                
                out.print("{\"videojuego_id\": " + videojuegoId + ", " +
                         "\"calificacion_promedio\": " + promedio + ", " +
                         "\"comentarios\": " + gson.toJson(comentarios) + "}");
                
            } else if (comentarioPadreIdParam != null) {
                // respuestas de un comentario
                int comentarioPadreId = Integer.parseInt(comentarioPadreIdParam);
                List<Comentario> respuestas = comentarioService.obtenerRespuestas(comentarioPadreId);
                
                out.print(gson.toJson(respuestas));
                
            } else {
                response.setStatus(400);
                out.print("{\"error\": \"Se requiere videojuego_id o comentario_padre_id\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
    
    // PUT: Moderar comentario 
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            int comentarioId = Integer.parseInt(request.getParameter("comentario_id"));
            boolean visible = Boolean.parseBoolean(request.getParameter("visible"));
            
            boolean moderado = comentarioService.moderarComentario(comentarioId, visible);
            
            if (moderado) {
                out.print("{\"success\": true, \"message\": \"Comentario moderado\"}");
            } else {
                response.setStatus(400);
                out.print("{\"error\": \"No se pudo moderar el comentario\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
    
    // DELETE: Eliminar comentario
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            int comentarioId = Integer.parseInt(request.getParameter("comentario_id"));
            
            boolean eliminado = comentarioService.eliminarComentario(comentarioId);
            
            if (eliminado) {
                out.print("{\"success\": true, \"message\": \"Comentario eliminado\"}");
            } else {
                response.setStatus(400);
                out.print("{\"error\": \"No se pudo eliminar el comentario\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
}