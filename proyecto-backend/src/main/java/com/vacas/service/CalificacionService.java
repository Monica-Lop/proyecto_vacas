package com.vacas.service;

import java.util.List;

import com.vacas.dao.CalificacionDAO;
import com.vacas.dao.VideojuegoDAO;
import com.vacas.model.Calificacion;

public class CalificacionService {
    private CalificacionDAO calificacionDAO;
    private VideojuegoDAO videojuegoDAO;
    
    public CalificacionService() {
        this.calificacionDAO = new CalificacionDAO();
        this.videojuegoDAO = new VideojuegoDAO();
    }
    
    public boolean calificar(Calificacion calificacion) {
        boolean resultado = calificacionDAO.crear(calificacion);
        
        if (resultado) {
            // Actualizar calificación promedio del videojuego
            double promedio = calificacionDAO.obtenerPromedio(calificacion.getVideojuegoId());
            // Aquí deberías actualizar el campo calificacion_promedio en la tabla videojuego
            // Necesitarías un método en VideojuegoDAO para actualizar la calificación
        }
        
        return resultado;
    }
    
    public List<Calificacion> obtenerCalificacionesPorVideojuego(int videojuegoId) {
        return calificacionDAO.obtenerPorVideojuego(videojuegoId);
    }
    
    public double obtenerPromedio(int videojuegoId) {
        return calificacionDAO.obtenerPromedio(videojuegoId);
    }
    
    public boolean eliminarCalificacion(int usuarioId, int videojuegoId) {
        return calificacionDAO.eliminar(usuarioId, videojuegoId);
    }
}