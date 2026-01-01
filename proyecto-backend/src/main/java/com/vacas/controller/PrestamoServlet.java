package com.vacas.controller;

import com.google.gson.Gson;
import com.vacas.model.Prestamo;
import com.vacas.service.PrestamoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/prestamos/*")
public class PrestamoServlet extends HttpServlet {
    
    private PrestamoService prestamoService = new PrestamoService();
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
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/prestamos - Historial de préstamos
                List<Prestamo> prestamos = prestamoService.obtenerHistorial(usuarioId);
                out.print(gson.toJson(prestamos));
            } else if (pathInfo.equals("/instalado")) {
                // GET /api/prestamos/instalado - Juego actualmente instalado
                Prestamo prestamo = prestamoService.obtenerJuegoInstalado(usuarioId);
                if (prestamo != null) {
                    out.print(gson.toJson(prestamo));
                } else {
                    out.print("{\"instalado\": false}");
                }
            }
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
            String accion = request.getParameter("accion"); // "instalar" o "desinstalar"
            
            if (videojuegoIdStr == null || accion == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Datos incompletos\"}");
                return;
            }
            
            int videojuegoId = Integer.parseInt(videojuegoIdStr);
            boolean resultado = false;
            
            if ("instalar".equalsIgnoreCase(accion)) {
                Prestamo prestamo = new Prestamo();
                prestamo.setUsuarioId(usuarioId);
                prestamo.setVideojuegoId(videojuegoId);
                resultado = prestamoService.instalarJuego(prestamo);
                
                if (!resultado) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"Ya tienes un juego instalado. Desinstálalo primero.\"}");
                    return;
                }
            } else if ("desinstalar".equalsIgnoreCase(accion)) {
                resultado = prestamoService.desinstalarJuego(usuarioId, videojuegoId);
            }
            
            if (resultado) {
                out.print("{\"success\": true, \"message\": \"Operación completada\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Error en la operación\"}");
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