package com.vacas.controller;

import com.google.gson.Gson;
import com.vacas.service.ReporteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@WebServlet("/api/reportes/*")
public class ReporteServlet extends HttpServlet {
    
    private ReporteService reporteService = new ReporteService();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"error\": \"No autenticado\"}");
            return;
        }
        
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/reportes/ganancias - Reporte de ganancias (solo admin)
                if (!"ADMIN".equals(tipoUsuario)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.print("{\"error\": \"No autorizado\"}");
                    return;
                }
                
                List<Map<String, Object>> ganancias = reporteService.obtenerGananciasGlobales();
                out.print(gson.toJson(ganancias));
            } else if (pathInfo.equals("/top-videojuegos")) {
                // GET /api/reportes/top-videojuegos - Top videojuegos
                String limiteStr = request.getParameter("limite");
                int limite = limiteStr != null ? Integer.parseInt(limiteStr) : 10;
                
                List<Map<String, Object>> top = reporteService.obtenerTopVideojuegos(limite);
                out.print(gson.toJson(top));
            } else if (pathInfo.equals("/ingresos-empresas")) {
                // GET /api/reportes/ingresos-empresas - Ingresos por empresa (solo admin)
                if (!"ADMIN".equals(tipoUsuario)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.print("{\"error\": \"No autorizado\"}");
                    return;
                }
                
                List<Map<String, Object>> ingresos = reporteService.obtenerIngresosPorEmpresa();
                out.print(gson.toJson(ingresos));
            } else if (pathInfo.equals("/ventas-empresa")) {
                // GET /api/reportes/ventas-empresa - Ventas de mi empresa
                if (!"EMPRESA".equals(tipoUsuario)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.print("{\"error\": \"No autorizado\"}");
                    return;
                }
                
                Integer empresaId = (Integer) session.getAttribute("empresaId");
                if (empresaId == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"No tienes empresa asignada\"}");
                    return;
                }
                
                String fechaInicioStr = request.getParameter("fechaInicio");
                String fechaFinStr = request.getParameter("fechaFin");
                
                Date fechaInicio = new Date(); // Hoy por defecto
                Date fechaFin = new Date();
                
                if (fechaInicioStr != null && fechaFinStr != null) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        fechaInicio = sdf.parse(fechaInicioStr);
                        fechaFin = sdf.parse(fechaFinStr);
                    } catch (ParseException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print("{\"error\": \"Formato de fecha inválido\"}");
                        return;
                    }
                }
                
                List<Map<String, Object>> ventas = reporteService.obtenerVentasPorEmpresa(empresaId, fechaInicio, fechaFin);
                out.print(gson.toJson(ventas));
            } else if (pathInfo.equals("/ranking-usuarios")) {
                // GET /api/reportes/ranking-usuarios - Ranking de usuarios (solo admin)
                if (!"ADMIN".equals(tipoUsuario)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.print("{\"error\": \"No autorizado\"}");
                    return;
                }
                
                List<Map<String, Object>> ranking = reporteService.obtenerRankingUsuarios();
                out.print(gson.toJson(ranking));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Parámetro inválido\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error interno del servidor\"}");
            e.printStackTrace();
        }
        out.flush();
    }
}