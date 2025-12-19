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
    
    private final Gson gson = new Gson();
    private final EmpresaService empresaService = new EmpresaService();
    
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
                List<Empresa> empresas = empresaService.listarEmpresas();
                out.print(gson.toJson(empresas));
            } else {
                // GET /api/empresas/{id} - Obtener por ID
                String idStr = pathInfo.substring(1); // quita el "/"
                int id = Integer.parseInt(idStr);
                Empresa empresa = empresaService.obtenerEmpresa(id);
                
                if (empresa != null) {
                    out.print(gson.toJson(empresa));
                } else {
                    response.setStatus(404);
                    out.print("{\"error\": \"Empresa no encontrada\"}");
                }
            }
        } catch (NumberFormatException e) {
            response.setStatus(400);
            out.print("{\"error\": \"ID inválido\"}");
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error interno: " + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
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
            String comisionStr = request.getParameter("comision");
            
            double comision = 15.0; 
            if (comisionStr != null && !comisionStr.isEmpty()) {
                comision = Double.parseDouble(comisionStr);
            }
            
            Empresa empresa = new Empresa();
            empresa.setNombre(nombre);
            empresa.setDescripcion(descripcion);
            empresa.setTelefono(telefono);
            empresa.setComision(comision);
            
            boolean creada = empresaService.crearEmpresa(empresa);
            
            if (creada) {
                out.print("{\"success\": true, \"message\": \"Empresa creada\"}");
            } else {
                response.setStatus(400);
                out.print("{\"success\": false, \"message\": \"Error al crear empresa\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"success\": false, \"message\": \"Error: " + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        //...
        response.setStatus(501); 
        response.setContentType("application/json");
        response.getWriter().print("{\"error\": \"Método no implementado\"}");
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.setStatus(400);
            out.print("{\"error\": \"ID requerido\"}");
            return;
        }
        
        try {
            String idStr = pathInfo.substring(1);
            int id = Integer.parseInt(idStr);
            
            boolean eliminada = empresaService.eliminarEmpresa(id);
            
            if (eliminada) {
                out.print("{\"success\": true, \"message\": \"Empresa eliminada\"}");
            } else {
                response.setStatus(404);
                out.print("{\"success\": false, \"message\": \"Empresa no encontrada\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(400);
            out.print("{\"error\": \"ID inválido\"}");
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error interno: " + e.getMessage() + "\"}");
        } finally {
            out.flush();
        }
    }
}