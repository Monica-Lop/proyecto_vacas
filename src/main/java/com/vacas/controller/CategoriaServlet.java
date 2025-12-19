package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import com.google.gson.Gson;
import com.vacas.model.Categoria;
import com.vacas.service.CategoriaService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/categorias")
public class CategoriaServlet extends HttpServlet {
    
    private CategoriaService categoriaService;
    private Gson gson;
    
    @Override
    public void init() {
        this.categoriaService = new CategoriaService();
        this.gson = new Gson();
    }
    
    // GET: Listar todas o una por ID
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String idParam = request.getParameter("id");
        
        if (idParam != null && !idParam.trim().isEmpty()) {
            // Obtener UNA categoría por ID
            int id = Integer.parseInt(idParam);
            Categoria categoria = categoriaService.obtenerCategoria(id);
            
            if (categoria != null) {
                out.print(gson.toJson(categoria));
            } else {
                response.setStatus(404);
                out.print("{\"error\": \"Categoría no encontrada\"}");
            }
            
        } else {
            // Listar TODAS las categorías
            List<Categoria> categorias = categoriaService.listarCategorias();
            out.print(gson.toJson(categorias));
        }
        
        out.flush();
    }
    
    // POST: Crear nueva categoría
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");
            String activaStr = request.getParameter("activa");
            
            if (nombre == null || nombre.trim().isEmpty()) {
                response.setStatus(400);
                out.print("{\"error\": \"El nombre es requerido\"}");
                return;
            }
            
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            categoria.setDescripcion(descripcion);
            categoria.setActiva(activaStr == null || Boolean.parseBoolean(activaStr));
            
            boolean creada = categoriaService.crearCategoria(categoria);
            
            if (creada) {
                out.print("{\"success\": true, \"message\": \"Categoría creada exitosamente\", \"id\": " + categoria.getId() + "}");
            } else {
                response.setStatus(500);
                out.print("{\"error\": \"No se pudo crear la categoría\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
    
    // PUT: Actualizar categoría
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String idParam = request.getParameter("id");
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");
            String activaStr = request.getParameter("activa");
            
            if (idParam == null || idParam.trim().isEmpty()) {
                response.setStatus(400);
                out.print("{\"error\": \"ID de categoría requerido\"}");
                return;
            }
            
            int id = Integer.parseInt(idParam);
            Categoria categoria = categoriaService.obtenerCategoria(id);
            
            if (categoria == null) {
                response.setStatus(404);
                out.print("{\"error\": \"Categoría no encontrada\"}");
                return;
            }
            
            if (nombre != null) categoria.setNombre(nombre);
            if (descripcion != null) categoria.setDescripcion(descripcion);
            if (activaStr != null) {
                categoria.setActiva(Boolean.parseBoolean(activaStr));
            }
            
            boolean actualizada = categoriaService.actualizarCategoria(categoria);
            
            if (actualizada) {
                out.print("{\"success\": true, \"message\": \"Categoría actualizada\"}");
            } else {
                response.setStatus(500);
                out.print("{\"error\": \"No se pudo actualizar la categoría\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
    
    // DELETE: Eliminar categoría (soft delete)
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String idParam = request.getParameter("id");
            
            if (idParam == null || idParam.trim().isEmpty()) {
                response.setStatus(400);
                out.print("{\"error\": \"ID de categoría requerido\"}");
                return;
            }
            
            int id = Integer.parseInt(idParam);
            boolean eliminada = categoriaService.eliminarCategoria(id);
            
            if (eliminada) {
                out.print("{\"success\": true, \"message\": \"Categoría eliminada\"}");
            } else {
                response.setStatus(500);
                out.print("{\"error\": \"No se pudo eliminar la categoría\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
}