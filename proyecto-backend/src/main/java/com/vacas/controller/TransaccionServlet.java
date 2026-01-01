package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.vacas.model.Transaccion;
import com.vacas.service.TransaccionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/api/transacciones/*")
public class TransaccionServlet extends HttpServlet {
    
    private TransaccionService transaccionService = new TransaccionService();
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
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/transacciones - Historial del usuario
                List<Transaccion> transacciones = transaccionService.obtenerHistorialUsuario(usuarioId);
                out.print(gson.toJson(transacciones));
            } else if (pathInfo.equals("/comisiones")) {
                // GET /api/transacciones/comisiones - Total de comisiones
                double totalComisiones = transaccionService.obtenerTotalComisiones();
                out.print("{\"totalComisiones\": " + totalComisiones + "}");
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
            String fechaCompraStr = request.getParameter("fechaCompra");
            
            if (videojuegoIdStr == null || videojuegoIdStr.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"ID de videojuego requerido\"}");
                return;
            }
            
            int videojuegoId = Integer.parseInt(videojuegoIdStr);
            Date fechaCompra;
            
            if (fechaCompraStr != null && !fechaCompraStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false); // Validación estricta
                fechaCompra = sdf.parse(fechaCompraStr);
            } else {
                fechaCompra = new Date();
            }
            
    
            boolean compraExitosa = transaccionService.comprarVideojuego(usuarioId, videojuegoId, fechaCompra);
            
            if (compraExitosa) {
                out.print("{\"success\": true, \"message\": \"Compra realizada exitosamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"No se pudo completar la compra\"}");
            }
        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Formato de fecha inválido. Use yyyy-MM-dd\"}");
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