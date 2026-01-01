package com.vacas.service;

import java.util.List;

import com.vacas.dao.VideojuegoDAO;
import com.vacas.model.Videojuego;

public class VideojuegoService {
    private VideojuegoDAO videojuegoDAO;
    
    public VideojuegoService() {
        this.videojuegoDAO = new VideojuegoDAO();
    }
    
    public boolean crearVideojuego(Videojuego videojuego) {
        return videojuegoDAO.crear(videojuego);
    }
    
    public List<Videojuego> obtenerTodos() {
        return videojuegoDAO.obtenerTodos();
    }
    
    public List<Videojuego> obtenerPorEmpresa(int empresaId) {
        return videojuegoDAO.obtenerPorEmpresa(empresaId);
    }
    
    public Videojuego obtenerPorId(int id) {
        return videojuegoDAO.obtenerPorId(id);
    }
    
    public boolean suspenderVenta(int id) {
        return videojuegoDAO.suspenderVenta(id);
    }
}