package com.vacas.service;

import java.util.List;

import com.vacas.dao.VideojuegoDAO;
import com.vacas.model.Videojuego;

public class VideojuegoService {
    private VideojuegoDAO videojuegoDAO;
    
    public VideojuegoService() {
        this.videojuegoDAO = new VideojuegoDAO();
    }
    
    // Crear videojuego con validaciones
    public boolean crearVideojuego(Videojuego videojuego) {
        if (videojuego.getTitulo() == null || videojuego.getTitulo().trim().isEmpty()) {
            System.out.println(" Título requerido");
            return false;
        }
        
        if (videojuego.getPrecio() < 0) {
            System.out.println(" Precio no puede ser negativo");
            return false;
        }
        
        if (videojuego.getEmpresaId() <= 0) {
            System.out.println("Empresa ID inválido");
            return false;
        }
        
        return videojuegoDAO.crear(videojuego);
    }
    
    public List<Videojuego> listarVideojuegos() {
        return videojuegoDAO.listarTodos();
    }
    
    public Videojuego obtenerVideojuego(int id) {
        return videojuegoDAO.buscarPorId(id);
    }
    
    public List<Videojuego> listarPorEmpresa(int empresaId) {
        return videojuegoDAO.listarPorEmpresa(empresaId);
    }
    
    public boolean actualizarVideojuego(Videojuego videojuego) {
        if (videojuego.getTitulo() == null || videojuego.getTitulo().trim().isEmpty()) {
            return false;
        }
        
        return videojuegoDAO.actualizar(videojuego);
    }
    
    public boolean eliminarVideojuego(int id) {
        return videojuegoDAO.eliminar(id);
    }
}