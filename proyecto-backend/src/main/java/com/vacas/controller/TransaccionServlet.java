package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.vacas.model.Transaccion;
import com.vacas.service.TransaccionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/transacciones")
public class TransaccionServlet extends HttpServlet {
    
    private TransaccionService transaccionService;
    private Gson gson;
    
    @Override
    public void init() {
        this.transaccionService = new TransaccionService();
        this.gson = new Gson();
    }
    
    // POST: Realizar compra o recargar cartera
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String action = request.getParameter("action");
        
        try {
            if ("compra".equals(action)) {
                // compra
                int usuarioId = Integer.parseInt(request.getParameter("usuario_id"));
                int videojuegoId = Integer.parseInt(request.getParameter("videojuego_id"));
                
                Transaccion transaccion = transaccionService.realizarCompra(usuarioId, videojuegoId);
                
                if (transaccion != null) {
                    out.print("{\"success\": true, \"message\": \"Compra realizada exitosamente\", " +
                             "\"transaccion_id\": " + transaccion.getId() + ", " +
                             "\"monto\": " + transaccion.getPrecioPagado() + "}");
                } else {
                    response.setStatus(400);
                    out.print("{\"error\": \"No se pudo realizar la compra\"}");
                }
                
            } else if ("recargar".equals(action)) {
                // recargar 
                int usuarioId = Integer.parseInt(request.getParameter("usuario_id"));
                double monto = Double.parseDouble(request.getParameter("monto"));
                
                boolean recargado = transaccionService.recargarCartera(usuarioId, monto);
                
                if (recargado) {
                    double nuevoSaldo = transaccionService.obtenerSaldo(usuarioId);
                    out.print("{\"success\": true, \"message\": \"Recarga exitosa\", " +
                             "\"nuevo_saldo\": " + nuevoSaldo + "}");
                } else {
                    response.setStatus(400);
                    out.print("{\"error\": \"No se pudo recargar la cartera\"}");
                }
                
            } else {
                response.setStatus(400);
                out.print("{\"error\": \"Acción no válida\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
    
    // GET: Obtener historial o saldo
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String usuarioIdParam = request.getParameter("usuario_id");
        String tipo = request.getParameter("tipo");
        
        try {
            if (usuarioIdParam != null) {
                int usuarioId = Integer.parseInt(usuarioIdParam);
                
                if ("historial".equals(tipo)) {
                    List<Transaccion> historial = transaccionService.obtenerHistorialUsuario(usuarioId);
                    out.print(gson.toJson(historial));
                    
                } else {
                    double saldo = transaccionService.obtenerSaldo(usuarioId);
                    out.print("{\"usuario_id\": " + usuarioId + ", \"saldo\": " + saldo + "}");
                }
                
            } else {
                response.setStatus(400);
                out.print("{\"error\": \"Se requiere usuario_id\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
}