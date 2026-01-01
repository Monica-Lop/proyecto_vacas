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

@WebServlet("/api/categorias/*")
public class CategoriaServlet extends HttpServlet {
    
    private CategoriaService categoriaService = new CategoriaService();
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            List<Categoria> categorias = categoriaService.obtenerTodas();
            out.print(gson.toJson(categorias));
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
        
        try {
            String nombre = request.getParameter("nombre");
            
            if (nombre == null || nombre.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"El nombre es requerido\"}");
                return;
            }
            
            Categoria categoria = new Categoria();
            categoria.setNombre(nombre);
            
            boolean creada = categoriaService.crear(categoria);
            
            if (creada) {
                out.print("{\"success\": true, \"message\": \"Categoría creada exitosamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Error al crear categoría\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Error interno del servidor\"}");
            e.printStackTrace();
        }
        out.flush();
    }
}

// CategoriaService.java (añadir al archivo de servicios)
class CategoriaService {
    private com.vacas.dao.CategoriaDAO categoriaDAO = new com.vacas.dao.CategoriaDAO();
    
    public List<Categoria> obtenerTodas() {
        return categoriaDAO.obtenerTodas();
    }
    
    public boolean crear(Categoria categoria) {
        return categoriaDAO.crear(categoria);
    }
}