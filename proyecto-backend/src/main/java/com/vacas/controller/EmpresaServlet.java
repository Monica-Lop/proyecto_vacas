package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.vacas.model.Empresa;
import com.vacas.service.EmpresaService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/empresas/*")
public class EmpresaServlet extends HttpServlet {
    
    private EmpresaService empresaService = new EmpresaService();
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
                // GET /api/empresas - Listar todas
                List<Empresa> empresas = empresaService.obtenerTodas();
                out.print(gson.toJson(empresas));
            } else if (pathInfo.matches("/\\d+")) {
                // GET /api/empresas/{id} - Obtener por ID
                int id = Integer.parseInt(pathInfo.substring(1));
                Empresa empresa = empresaService.obtenerPorId(id);
                
                if (empresa != null) {
                    out.print(gson.toJson(empresa));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Empresa no encontrada\"}");
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
        
        try {
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");
            String telefono = request.getParameter("telefono");
            
            if (nombre == null || nombre.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"El nombre es requerido\"}");
                return;
            }
            
            Empresa empresa = new Empresa();
            empresa.setNombre(nombre);
            empresa.setDescripcion(descripcion != null ? descripcion : "");
            empresa.setTelefono(telefono != null ? telefono : "");
            
            boolean creada = empresaService.crear(empresa);
            
            if (creada) {
                out.print("{\"success\": true, \"message\": \"Empresa creada exitosamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Error al crear empresa\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Error interno del servidor\"}");
            e.printStackTrace();
        }
        out.flush();
    }
}

