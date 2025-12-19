package com.vacas.service;

import java.util.List;

import com.vacas.dao.ComentarioDAO;
import com.vacas.dao.UsuarioDAO;
import com.vacas.dao.VideojuegoDAO;
import com.vacas.model.Comentario;

public class ComentarioService {
    private ComentarioDAO comentarioDAO;
    private UsuarioDAO usuarioDAO;
    private VideojuegoDAO videojuegoDAO;
    
    public ComentarioService() {
        this.comentarioDAO = new ComentarioDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.videojuegoDAO = new VideojuegoDAO();
    }
    
    // CREAR COMENTARIO/CALIFICACIÓN
    public Comentario crearComentario(int usuarioId, int videojuegoId, String texto, int calificacion, Integer comentarioPadreId) {
        System.out.println("💬 Creando comentario - Usuario: " + usuarioId + ", Videojuego: " + videojuegoId);
        
        // Validar que usuario y videojuego existen
        if (usuarioDAO.buscarPorId(usuarioId) == null) {
            System.out.println("❌ Usuario no encontrado");
            return null;
        }
        
        if (videojuegoDAO.buscarPorId(videojuegoId) == null) {
            System.out.println("❌ Videojuego no encontrado");
            return null;
        }
        
        // Validar calificación
        if (calificacion < 1 || calificacion > 5) {
            System.out.println("❌ Calificación debe ser entre 1 y 5");
            return null;
        }
        
        // Crear comentario
        Comentario comentario = new Comentario(usuarioId, videojuegoId, texto, calificacion);
        if (comentarioPadreId != null) {
            comentario.setComentarioPadreId(comentarioPadreId);
        }
        
        boolean creado = comentarioDAO.crear(comentario);
        
        if (creado) {
            System.out.println("✅ Comentario creado ID: " + comentario.getId());
            return comentario;
        } else {
            System.out.println("❌ Error al crear comentario");
            return null;
        }
    }
    
    // OBTENER COMENTARIOS DE VIDEOJUEGO
    public List<Comentario> obtenerComentariosVideojuego(int videojuegoId) {
        return comentarioDAO.listarPorVideojuego(videojuegoId);
    }
    
    // OBTENER RESPUESTAS
    public List<Comentario> obtenerRespuestas(int comentarioPadreId) {
        return comentarioDAO.listarRespuestas(comentarioPadreId);
    }
    
    // OBTENER CALIFICACIÓN PROMEDIO
    public double obtenerCalificacionPromedio(int videojuegoId) {
        return comentarioDAO.obtenerCalificacionPromedio(videojuegoId);
    }
    
    // MODERAR COMENTARIO (empresa puede ocultar)
    public boolean moderarComentario(int comentarioId, boolean visible) {
        Comentario comentario = comentarioDAO.obtenerPorId(comentarioId);
        if (comentario == null) {
            System.out.println("❌ Comentario no encontrado");
            return false;
        }
        
        return comentarioDAO.actualizarVisibilidad(comentarioId, visible);
    }
    
    // ELIMINAR COMENTARIO
    public boolean eliminarComentario(int comentarioId) {
        return comentarioDAO.eliminar(comentarioId);
    }
}