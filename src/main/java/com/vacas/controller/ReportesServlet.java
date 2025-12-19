package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.vacas.service.ReportesService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/reportes/*")
public class ReportesServlet extends HttpServlet {
    
    private ReportesService reportesService;
    private Gson gson;
    
    @Override
    public void init() {
        this.reportesService = new ReportesService();
        this.gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null) pathInfo = "/";
        
        try {
            switch (pathInfo) {
                case "/":
                    String[] reportes = {
                        "ganancias", "ingresos-empresa", "top-ventas-calidad", 
                        "ranking-usuarios", "ventas-propias", "feedback", 
                        "top5-empresa", "historial-gastos", "analisis-biblioteca"
                    };
                    out.print(gson.toJson(reportes));
                    break;
                    
                case "/ganancias":
                    // Reporte de ganancias globales
                    String fechaInicio = request.getParameter("fecha_inicio");
                    String fechaFin = request.getParameter("fecha_fin");
                    
                    if (fechaInicio == null || fechaFin == null) {
                        fechaInicio = "2025-12-01";
                        fechaFin = "2025-12-31";
                    }
                    
                    Map<String, Object> ganancias = reportesService.generarReporteGanancias(fechaInicio, fechaFin);
                    out.print(gson.toJson(ganancias));
                    break;
                    
                case "/ingresos-empresa":
                    // Reporte de ingresos por empresa
                    fechaInicio = request.getParameter("fecha_inicio");
                    fechaFin = request.getParameter("fecha_fin");
                    
                    if (fechaInicio == null || fechaFin == null) {
                        fechaInicio = "2025-12-01";
                        fechaFin = "2025-12-31";
                    }
                    
                    List<Map<String, Object>> ingresosEmpresa = reportesService.generarReporteIngresosPorEmpresa(fechaInicio, fechaFin);
                    out.print(gson.toJson(ingresosEmpresa));
                    break;
                    
                case "/top-ventas-calidad":
                    // Top ventas y calidad
                    String limiteStr = request.getParameter("limite");
                    int limite = limiteStr != null ? Integer.parseInt(limiteStr) : 10;
                    
                    List<Map<String, Object>> topVentasCalidad = reportesService.generarTopVentasCalidad(limite);
                    out.print(gson.toJson(topVentasCalidad));
                    break;
                    
                case "/ranking-usuarios":
                    // Ranking de usuarios
                    Map<String, List<Map<String, Object>>> rankingUsuarios = reportesService.generarRankingUsuarios();
                    out.print(gson.toJson(rankingUsuarios));
                    break;
                    
                case "/ventas-propias":
                    // Reporte de ventas propias 
                    String empresaIdStr = request.getParameter("empresa_id");
                    fechaInicio = request.getParameter("fecha_inicio");
                    fechaFin = request.getParameter("fecha_fin");
                    
                    if (empresaIdStr == null) {
                        response.setStatus(400);
                        out.print("{\"error\": \"Se requiere empresa_id\"}");
                        return;
                    }
                    
                    if (fechaInicio == null || fechaFin == null) {
                        fechaInicio = "2025-12-01";
                        fechaFin = "2025-12-31";
                    }
                    
                    int empresaId = Integer.parseInt(empresaIdStr);
                    List<Map<String, Object>> ventasPropias = reportesService.generarReporteVentasPropias(empresaId, fechaInicio, fechaFin);
                    out.print(gson.toJson(ventasPropias));
                    break;
                    
                case "/feedback":
                    // Reporte de feedback
                    empresaIdStr = request.getParameter("empresa_id");
                    
                    if (empresaIdStr == null) {
                        response.setStatus(400);
                        out.print("{\"error\": \"Se requiere empresa_id\"}");
                        return;
                    }
                    
                    empresaId = Integer.parseInt(empresaIdStr);
                    Map<String, Object> feedback = reportesService.generarReporteFeedback(empresaId);
                    out.print(gson.toJson(feedback));
                    break;
                    
                case "/top5-empresa":
                    // Top 5 juegos por empresa
                    empresaIdStr = request.getParameter("empresa_id");
                    fechaInicio = request.getParameter("fecha_inicio");
                    fechaFin = request.getParameter("fecha_fin");
                    
                    if (empresaIdStr == null) {
                        response.setStatus(400);
                        out.print("{\"error\": \"Se requiere empresa_id\"}");
                        return;
                    }
                    
                    if (fechaInicio == null || fechaFin == null) {
                        fechaInicio = "2025-12-01";
                        fechaFin = "2025-12-31";
                    }
                    
                    empresaId = Integer.parseInt(empresaIdStr);
                    List<Map<String, Object>> top5Empresa = reportesService.generarTop5JuegosEmpresa(empresaId, fechaInicio, fechaFin);
                    out.print(gson.toJson(top5Empresa));
                    break;
                    
                case "/historial-gastos":
                    // Historial de gastos de usuario
                    String usuarioIdStr = request.getParameter("usuario_id");
                    
                    if (usuarioIdStr == null) {
                        response.setStatus(400);
                        out.print("{\"error\": \"Se requiere usuario_id\"}");
                        return;
                    }
                    
                    int usuarioId = Integer.parseInt(usuarioIdStr);
                    List<Map<String, Object>> historialGastos = reportesService.generarHistorialGastosUsuario(usuarioId);
                    out.print(gson.toJson(historialGastos));
                    break;
                    
                case "/analisis-biblioteca":
                    // Análisis de biblioteca de usuario
                    usuarioIdStr = request.getParameter("usuario_id");
                    
                    if (usuarioIdStr == null) {
                        response.setStatus(400);
                        out.print("{\"error\": \"Se requiere usuario_id\"}");
                        return;
                    }
                    
                    usuarioId = Integer.parseInt(usuarioIdStr);
                    Map<String, Object> analisisBiblioteca = reportesService.generarAnalisisBibliotecaUsuario(usuarioId);
                    out.print(gson.toJson(analisisBiblioteca));
                    break;
                    
                default:
                    response.setStatus(404);
                    out.print("{\"error\": \"Reporte no encontrado\"}");
                    break;
            }
            
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error generando reporte: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
}