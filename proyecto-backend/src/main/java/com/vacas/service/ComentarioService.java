package com.vacas.service;

import java.util.List;

import com.vacas.dao.ComentarioDAO;
import com.vacas.model.Comentario;

public class ComentarioService {
    private ComentarioDAO comentarioDAO;
    
    public ComentarioService() {
        this.comentarioDAO = new ComentarioDAO();
    }
    
    public boolean crearComentario(Comentario comentario) {
        return comentarioDAO.crear(comentario);
    }
    
    public List<Comentario> obtenerComentariosPorVideojuego(int videojuegoId) {
        return comentarioDAO.obtenerPorVideojuego(videojuegoId);
    }
    
    public boolean ocultarComentario(int id) {
        return comentarioDAO.ocultarComentario(id);
    }
    
    public boolean eliminarComentario(int id) {
        return comentarioDAO.eliminar(id);
    }
}