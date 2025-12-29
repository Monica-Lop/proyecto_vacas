package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import com.google.gson.Gson;
import com.vacas.model.Videojuego;
import com.vacas.service.VideojuegoService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/videojuegos")
public class VideojuegoServlet extends HttpServlet {
    
    private VideojuegoService videojuegoService;
    private Gson gson;
    
    @Override
    public void init() {
        this.videojuegoService = new VideojuegoService();
        this.gson = new Gson();
    }
    
    // GET: Listar todos, por ID o por empresa
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        String idParam = request.getParameter("id");
        String empresaIdParam = request.getParameter("empresaId");
        
        if (idParam != null && !idParam.trim().isEmpty()) {
            int id = Integer.parseInt(idParam);
            Videojuego videojuego = videojuegoService.obtenerVideojuego(id);
            
            if (videojuego != null) {
                out.print(gson.toJson(videojuego));
            } else {
                response.setStatus(404);
                out.print("{\"error\": \"Videojuego no encontrado\"}");
            }
            
        } else if (empresaIdParam != null && !empresaIdParam.trim().isEmpty()) {
            int empresaId = Integer.parseInt(empresaIdParam);
            List<Videojuego> videojuegos = videojuegoService.listarPorEmpresa(empresaId);
            out.print(gson.toJson(videojuegos));
            
        } else {
            List<Videojuego> videojuegos = videojuegoService.listarVideojuegos();
            out.print(gson.toJson(videojuegos));
        }
        
        out.flush();
    }
    
    // POST: Crear nuevo videojuego
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String titulo = request.getParameter("titulo");
            String descripcion = request.getParameter("descripcion");
            String precioStr = request.getParameter("precio");
            String edadMinimaStr = request.getParameter("edadMinima");
            String requisitos = request.getParameter("requisitos");
            String disponibleStr = request.getParameter("disponible");
            String fechaLanzamientoStr = request.getParameter("fechaLanzamiento");
            String empresaIdStr = request.getParameter("empresaId");
            
            if (titulo == null || titulo.trim().isEmpty() || 
                precioStr == null || empresaIdStr == null) {
                response.setStatus(400);
                out.print("{\"error\": \"Título, precio y empresa son requeridos\"}");
                return;
            }
            
            // Crear  Videojuego
            Videojuego videojuego = new Videojuego();
            videojuego.setTitulo(titulo);
            videojuego.setDescripcion(descripcion);
            videojuego.setPrecio(Double.parseDouble(precioStr));
            
            if (edadMinimaStr != null && !edadMinimaStr.trim().isEmpty()) {
                videojuego.setEdadMinima(Integer.parseInt(edadMinimaStr));
            }
            
            videojuego.setRequisitos(requisitos);
            videojuego.setDisponible(disponibleStr == null || Boolean.parseBoolean(disponibleStr));
            
            if (fechaLanzamientoStr != null && !fechaLanzamientoStr.trim().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                videojuego.setFechaLanzamiento(sdf.parse(fechaLanzamientoStr));
            }
            
            videojuego.setEmpresaId(Integer.parseInt(empresaIdStr));
            
            // Guardar
            boolean creado = videojuegoService.crearVideojuego(videojuego);
            
            if (creado) {
                out.print("{\"success\": true, \"message\": \"Videojuego creado exitosamente\", \"id\": " + videojuego.getId() + "}");
            } else {
                response.setStatus(500);
                out.print("{\"error\": \"No se pudo crear el videojuego\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
    
    // PUT: Actualizar videojuego
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            String idParam = request.getParameter("id");
            String titulo = request.getParameter("titulo");
            String descripcion = request.getParameter("descripcion");
            String precioStr = request.getParameter("precio");
            String edadMinimaStr = request.getParameter("edadMinima");
            String requisitos = request.getParameter("requisitos");
            String disponibleStr = request.getParameter("disponible");
            String fechaLanzamientoStr = request.getParameter("fechaLanzamiento");
            
            if (idParam == null || idParam.trim().isEmpty()) {
                response.setStatus(400);
                out.print("{\"error\": \"ID de videojuego requerido\"}");
                return;
            }
            
            int id = Integer.parseInt(idParam);
            Videojuego videojuego = videojuegoService.obtenerVideojuego(id);
            
            if (videojuego == null) {
                response.setStatus(404);
                out.print("{\"error\": \"Videojuego no encontrado\"}");
                return;
            }
            
            if (titulo != null) videojuego.setTitulo(titulo);
            if (descripcion != null) videojuego.setDescripcion(descripcion);
            if (precioStr != null && !precioStr.trim().isEmpty()) {
                videojuego.setPrecio(Double.parseDouble(precioStr));
            }
            if (edadMinimaStr != null && !edadMinimaStr.trim().isEmpty()) {
                videojuego.setEdadMinima(Integer.parseInt(edadMinimaStr));
            }
            if (requisitos != null) videojuego.setRequisitos(requisitos);
            if (disponibleStr != null) {
                videojuego.setDisponible(Boolean.parseBoolean(disponibleStr));
            }
            if (fechaLanzamientoStr != null && !fechaLanzamientoStr.trim().isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                videojuego.setFechaLanzamiento(sdf.parse(fechaLanzamientoStr));
            }
            
            boolean actualizado = videojuegoService.actualizarVideojuego(videojuego);
            
            if (actualizado) {
                out.print("{\"success\": true, \"message\": \"Videojuego actualizado\"}");
            } else {
                response.setStatus(500);
                out.print("{\"error\": \"No se pudo actualizar el videojuego\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
    
    // DELETE: Eliminar videojuego (soft delete)
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
                out.print("{\"error\": \"ID de videojuego requerido\"}");
                return;
            }
            
            int id = Integer.parseInt(idParam);
            boolean eliminado = videojuegoService.eliminarVideojuego(id);
            
            if (eliminado) {
                out.print("{\"success\": true, \"message\": \"Videojuego eliminado\"}");
            } else {
                response.setStatus(500);
                out.print("{\"error\": \"No se pudo eliminar el videojuego\"}");
            }
            
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"error\": \"Error: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
        
        out.flush();
    }
}