package com.vacas.service;

import java.util.List;

import com.vacas.dao.PrestamoDAO;
import com.vacas.model.Prestamo;

public class PrestamoService {
    private PrestamoDAO prestamoDAO;
    
    public PrestamoService() {
        this.prestamoDAO = new PrestamoDAO();
    }
    
    public boolean instalarJuego(Prestamo prestamo) {
        // Verificar si ya tiene un juego instalado
        Prestamo instalado = prestamoDAO.obtenerInstaladoActualmente(prestamo.getUsuarioId());
        
        if (instalado != null) {
            // Ya tiene un juego instalado, debe desinstalarlo primero
            return false;
        }
        
        return prestamoDAO.instalar(prestamo);
    }
    
    public boolean desinstalarJuego(int usuarioId, int videojuegoId) {
        return prestamoDAO.desinstalar(usuarioId, videojuegoId);
    }
    
    public Prestamo obtenerJuegoInstalado(int usuarioId) {
        return prestamoDAO.obtenerInstaladoActualmente(usuarioId);
    }
    
    public List<Prestamo> obtenerHistorial(int usuarioId) {
        return prestamoDAO.obtenerHistorial(usuarioId);
    }
}