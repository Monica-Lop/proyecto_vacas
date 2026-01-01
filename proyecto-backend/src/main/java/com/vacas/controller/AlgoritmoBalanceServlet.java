package com.vacas.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vacas.utils.DatabaseConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/algoritmo/mejor-balance")
public class AlgoritmoBalanceServlet extends HttpServlet {
    
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // Parámetro opcional para cantidad de resultados
            int limite = 10;
            String limiteStr = request.getParameter("limite");
            if (limiteStr != null) {
                limite = Integer.parseInt(limiteStr);
            }
            
            // Algoritmo: score = (ventas * 0.4) + (calificación * 20 * 0.6)
            // Donde calificación va de 0-5, la normalizamos multiplicando por 20 para que sea comparable con ventas
            String sql = "SELECT v.id, v.titulo, e.nombre as empresa, " +
                        "  v.calificacion_promedio, " +
                        "  COUNT(t.id) as ventas, " +
                        "  (COUNT(t.id) * 0.4 + v.calificacion_promedio * 20 * 0.6) as score " +
                        "FROM videojuego v " +
                        "LEFT JOIN empresa e ON v.empresa_id = e.id " +
                        "LEFT JOIN transaccion t ON v.id = t.videojuego_id " +
                        "WHERE v.disponible = TRUE " +
                        "GROUP BY v.id, v.titulo, e.nombre, v.calificacion_promedio " +
                        "HAVING ventas > 0 OR v.calificacion_promedio > 0 " +
                        "ORDER BY score DESC " +
                        "LIMIT ?";
        
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, limite);
                ResultSet rs = stmt.executeQuery();
                
                JsonArray resultados = new JsonArray();
                
                while (rs.next()) {
                    JsonObject juego = new JsonObject();
                    juego.addProperty("id", rs.getInt("id"));
                    juego.addProperty("titulo", rs.getString("titulo"));
                    juego.addProperty("empresa", rs.getString("empresa"));
                    juego.addProperty("calificacionPromedio", rs.getDouble("calificacion_promedio"));
                    juego.addProperty("ventas", rs.getInt("ventas"));
                    juego.addProperty("score", rs.getDouble("score"));
                    resultados.add(juego);
                }
                
                JsonObject respuesta = new JsonObject();
                respuesta.add("juegosRecomendados", resultados);
                respuesta.addProperty("algoritmo", "Mejor Balance: score = (ventas * 0.4) + (calificación * 20 * 0.6)");
                respuesta.addProperty("totalResultados", resultados.size());
                
                out.print(gson.toJson(respuesta));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"Error interno del servidor\"}");
            e.printStackTrace();
        }
    }
}