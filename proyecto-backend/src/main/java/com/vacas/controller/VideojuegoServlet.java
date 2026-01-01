package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.vacas.model.Videojuego;
import com.vacas.service.VideojuegoService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/videojuegos/*")
public class VideojuegoServlet extends HttpServlet {
    
    private VideojuegoService videojuegoService = new VideojuegoService();
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
                // GET /api/videojuegos - Listar todos
                List<Videojuego> videojuegos = videojuegoService.obtenerTodos();
                out.print(gson.toJson(videojuegos));
            } else if (pathInfo.matches("/\\d+")) {
                // GET /api/videojuegos/{id} - Obtener por ID
                int id = Integer.parseInt(pathInfo.substring(1));
                Videojuego videojuego = videojuegoService.obtenerPorId(id);
                
                if (videojuego != null) {
                    out.print(gson.toJson(videojuego));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"error\": \"Videojuego no encontrado\"}");
                }
            } else if (pathInfo.equals("/empresa")) {
                // GET /api/videojuegos/empresa?empresaId=X
                String empresaIdStr = request.getParameter("empresaId");
                if (empresaIdStr != null) {
                    int empresaId = Integer.parseInt(empresaIdStr);
                    List<Videojuego> videojuegos = videojuegoService.obtenerPorEmpresa(empresaId);
                    out.print(gson.toJson(videojuegos));
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Parámetro empresaId requerido\"}");
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
            // Obtener parámetros del formulario
            String titulo = request.getParameter("titulo");
            String descripcion = request.getParameter("descripcion");
            String precioStr = request.getParameter("precio");
            String requisitos = request.getParameter("requisitos");
            String edadMinimaStr = request.getParameter("edadMinima");
            String fechaLanzamientoStr = request.getParameter("fechaLanzamiento");
            String empresaIdStr = request.getParameter("empresaId");
            
            // Validar parámetros requeridos
            if (titulo == null || descripcion == null || precioStr == null || 
                requisitos == null || edadMinimaStr == null || 
                fechaLanzamientoStr == null || empresaIdStr == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Todos los campos son requeridos\"}");
                return;
            }
            
            // Convertir tipos
            double precio = Double.parseDouble(precioStr);
            int edadMinima = Integer.parseInt(edadMinimaStr);
            int empresaId = Integer.parseInt(empresaIdStr);
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaLanzamiento = sdf.parse(fechaLanzamientoStr);
            
            // Crear objeto Videojuego
            Videojuego videojuego = new Videojuego();
            videojuego.setTitulo(titulo);
            videojuego.setDescripcion(descripcion);
            videojuego.setPrecio(precio);
            videojuego.setRequisitos(requisitos);
            videojuego.setEdadMinima(edadMinima);
            videojuego.setFechaLanzamiento(fechaLanzamiento);
            videojuego.setEmpresaId(empresaId);
            videojuego.setDisponible(true);
            
            // Guardar en base de datos
            boolean creado = videojuegoService.crearVideojuego(videojuego);
            
            if (creado) {
                out.print("{\"success\": true, \"message\": \"Videojuego creado exitosamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Error al crear videojuego\"}");
            }
        } catch (ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Formato de fecha inválido\"}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Formato numérico inválido\"}");
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
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || !pathInfo.matches("/\\d+")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"ID inválido\"}");
            return;
        }
        
        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            boolean suspendido = videojuegoService.suspenderVenta(id);
            
            if (suspendido) {
                out.print("{\"success\": true, \"message\": \"Venta suspendida exitosamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"success\": false, \"message\": \"Videojuego no encontrado\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Error interno del servidor\"}");
            e.printStackTrace();
        }
        out.flush();
    }
}