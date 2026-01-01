package com.vacas.controller;

import com.google.gson.Gson;
import com.vacas.model.ComisionEspecial;
import com.vacas.model.ComisionGlobal;
import com.vacas.service.ComisionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/api/comisiones/*")
public class ComisionServlet extends HttpServlet {
    
    private ComisionService comisionService = new ComisionService();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/comisiones - Comisión global actual
                ComisionGlobal comision = comisionService.obtenerComisionGlobalActual();
                out.print(gson.toJson(comision));
            } else if (pathInfo.equals("/especiales")) {
                // GET /api/comisiones/especiales - Todas las comisiones especiales
                // Solo admin
                if (session == null || !"ADMIN".equals(session.getAttribute("tipoUsuario"))) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.print("{\"error\": \"No autorizado\"}");
                    return;
                }
                
                List<ComisionEspecial> comisiones = comisionService.obtenerTodasComisionesEspeciales();
                out.print(gson.toJson(comisiones));
            } else if (pathInfo.matches("/especial/\\d+")) {
                // GET /api/comisiones/especial/{empresaId} - Comisión especial de una empresa
                int empresaId = Integer.parseInt(pathInfo.split("/")[2]);
                ComisionEspecial comision = comisionService.obtenerComisionEspecial(empresaId);
                
                if (comision != null) {
                    out.print(gson.toJson(comision));
                } else {
                    out.print("{\"existe\": false}");
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
        if (session == null || !"ADMIN".equals(session.getAttribute("tipoUsuario"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"No autorizado\"}");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // POST /api/comisiones - Establecer comisión global
                String porcentajeStr = request.getParameter("porcentaje");
                
                if (porcentajeStr == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"Porcentaje requerido\"}");
                    return;
                }
                
                double porcentaje = Double.parseDouble(porcentajeStr);
                
                if (porcentaje < 0 || porcentaje > 100) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"Porcentaje debe estar entre 0 y 100\"}");
                    return;
                }
                
                // Ajustar comisiones especiales si es necesario
                comisionService.ajustarComisionesEspeciales(porcentaje);
                
                ComisionGlobal comision = new ComisionGlobal();
                comision.setPorcentaje(porcentaje);
                
                boolean establecida = comisionService.establecerComisionGlobal(comision);
                
                if (establecida) {
                    out.print("{\"success\": true, \"message\": \"Comisión global actualizada\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"success\": false, \"message\": \"Error al actualizar comisión\"}");
                }
            } else if (pathInfo.equals("/especial")) {
                // POST /api/comisiones/especial - Establecer comisión especial
                String empresaIdStr = request.getParameter("empresaId");
                String porcentajeStr = request.getParameter("porcentaje");
                
                if (empresaIdStr == null || porcentajeStr == null) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"Datos incompletos\"}");
                    return;
                }
                
                int empresaId = Integer.parseInt(empresaIdStr);
                double porcentaje = Double.parseDouble(porcentajeStr);
                
                // Verificar que no sea mayor a la comisión global
                ComisionGlobal global = comisionService.obtenerComisionGlobalActual();
                if (global != null && porcentaje > global.getPorcentaje()) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\": false, \"message\": \"La comisión especial no puede ser mayor a la global (" + global.getPorcentaje() + "%)\"}");
                    return;
                }
                
                ComisionEspecial comision = new ComisionEspecial();
                comision.setEmpresaId(empresaId);
                comision.setPorcentaje(porcentaje);
                
                boolean establecida = comisionService.establecerComisionEspecial(comision);
                
                if (establecida) {
                    out.print("{\"success\": true, \"message\": \"Comisión especial establecida\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"success\": false, \"message\": \"Error al establecer comisión\"}");
                }
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
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("tipoUsuario"))) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"No autorizado\"}");
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || !pathInfo.matches("/especial/\\d+")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Ruta inválida\"}");
            return;
        }
        
        try {
            int empresaId = Integer.parseInt(pathInfo.split("/")[2]);
            boolean eliminada = comisionService.eliminarComisionEspecial(empresaId);
            
            if (eliminada) {
                out.print("{\"success\": true, \"message\": \"Comisión especial eliminada\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Comisión no encontrada\"}");
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