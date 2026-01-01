package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.vacas.model.Calificacion;
import com.vacas.service.CalificacionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/calificaciones/*")
public class CalificacionServlet extends HttpServlet {
    
    private CalificacionService calificacionService = new CalificacionService();
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
                // GET /api/calificaciones?videojuegoId=X
                String videojuegoIdStr = request.getParameter("videojuegoId");
                if (videojuegoIdStr != null) {
                    int videojuegoId = Integer.parseInt(videojuegoIdStr);
                    List<Calificacion> calificaciones = calificacionService.obtenerCalificacionesPorVideojuego(videojuegoId);
                    out.print(gson.toJson(calificaciones));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Parámetro videojuegoId requerido\"}");
                }
            } else if (pathInfo.matches("/\\d+/promedio")) {
                // GET /api/calificaciones/{id}/promedio
                int videojuegoId = Integer.parseInt(pathInfo.split("/")[1]);
                double promedio = calificacionService.obtenerPromedio(videojuegoId);
                out.print("{\"promedio\": " + promedio + "}");
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
            String estrellasStr = request.getParameter("estrellas");
            
            if (videojuegoIdStr == null || estrellasStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Datos incompletos\"}");
                return;
            }
            
            int videojuegoId = Integer.parseInt(videojuegoIdStr);
            int estrellas = Integer.parseInt(estrellasStr);
            
            if (estrellas < 1 || estrellas > 5) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Las estrellas deben estar entre 1 y 5\"}");
                return;
            }
            
            Calificacion calificacion = new Calificacion();
            calificacion.setUsuarioId(usuarioId);
            calificacion.setVideojuegoId(videojuegoId);
            calificacion.setEstrellas(estrellas);
            
            boolean calificado = calificacionService.calificar(calificacion);
            
            if (calificado) {
                out.print("{\"success\": true, \"message\": \"Calificación registrada\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Error al registrar calificación\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Datos inválidos\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Error interno del servidor\"}");
            e.printStackTrace();
        }
        out.flush();
    }
}