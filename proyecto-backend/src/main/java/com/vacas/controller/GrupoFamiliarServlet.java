package com.vacas.controller;

import com.google.gson.Gson;
import com.vacas.model.GrupoFamiliar;
import com.vacas.model.MiembroGrupo;
import com.vacas.service.GrupoFamiliarService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/grupos/*")
public class GrupoFamiliarServlet extends HttpServlet {
    
    private GrupoFamiliarService grupoService = new GrupoFamiliarService();
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
            List<GrupoFamiliar> grupos = grupoService.obtenerGruposPorUsuario(usuarioId);
            out.print(gson.toJson(grupos));
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
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // POST /api/grupos - Crear grupo
                String nombre = request.getParameter("nombre");
                
                if (nombre == null || nombre.trim().isEmpty()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"Nombre del grupo requerido\"}");
                    return;
                }
                
                GrupoFamiliar grupo = new GrupoFamiliar();
                grupo.setNombre(nombre);
                grupo.setAdminId(usuarioId);
                
                int grupoId = grupoService.crearGrupo(grupo);
                
                if (grupoId > 0) {
                    out.print("{\"success\": true, \"message\": \"Grupo creado\", \"grupoId\": " + grupoId + "}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"success\": false, \"message\": \"Error al crear grupo\"}");
                }
            } else if (pathInfo.matches("/\\d+/invitar")) {
                // POST /api/grupos/{id}/invitar - Invitar miembro
                int grupoId = Integer.parseInt(pathInfo.split("/")[1]);
                String usuarioInvitarIdStr = request.getParameter("usuarioId");
                
                if (usuarioInvitarIdStr == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"ID de usuario requerido\"}");
                    return;
                }
                
                int usuarioInvitarId = Integer.parseInt(usuarioInvitarIdStr);
                boolean invitado = grupoService.invitarMiembro(grupoId, usuarioInvitarId);
                
                if (invitado) {
                    out.print("{\"success\": true, \"message\": \"Invitación enviada\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"success\": false, \"message\": \"Error al enviar invitación\"}");
                }
            } else if (pathInfo.matches("/\\d+/aceptar")) {
                // POST /api/grupos/{id}/aceptar - Aceptar invitación
                int grupoId = Integer.parseInt(pathInfo.split("/")[1]);
                boolean aceptado = grupoService.aceptarInvitacion(grupoId, usuarioId);
                
                if (aceptado) {
                    out.print("{\"success\": true, \"message\": \"Invitación aceptada\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"success\": false, \"message\": \"Error al aceptar invitación\"}");
                }
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