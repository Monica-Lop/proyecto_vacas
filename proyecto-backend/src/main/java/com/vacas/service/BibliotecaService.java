package com.vacas.service;

import java.util.List;

import com.vacas.dao.BibliotecaDAO;
import com.vacas.model.Biblioteca;

public class BibliotecaService {
    private BibliotecaDAO bibliotecaDAO;
    
    public BibliotecaService() {
        this.bibliotecaDAO = new BibliotecaDAO();
    }
    
    public boolean agregarJuego(Biblioteca biblioteca) {
        // Verificar si ya tiene el juego
        if (bibliotecaDAO.existe(biblioteca.getUsuarioId(), biblioteca.getVideojuegoId())) {
            return false;
        }
        
        return bibliotecaDAO.agregar(biblioteca);
    }
    
    public List<Biblioteca> obtenerBibliotecaUsuario(int usuarioId) {
        return bibliotecaDAO.obtenerPorUsuario(usuarioId);
    }
    
    public boolean tieneJuego(int usuarioId, int videojuegoId) {
        return bibliotecaDAO.existe(usuarioId, videojuegoId);
    }
}