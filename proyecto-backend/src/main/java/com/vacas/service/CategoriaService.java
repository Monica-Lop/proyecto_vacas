package com.vacas.service;

import java.util.List;

import com.vacas.dao.CategoriaDAO;
import com.vacas.model.Categoria;

public class CategoriaService {
    private CategoriaDAO categoriaDAO = new CategoriaDAO();
    
    public boolean crear(Categoria categoria) {
        // Verificar si ya existe una categoría con ese nombre
        if (categoriaDAO.buscarPorNombre(categoria.getNombre()) != null) {
            return false;
        }
        return categoriaDAO.crear(categoria);
    }
    
    public List<Categoria> obtenerTodas() {
        return categoriaDAO.obtenerTodas();
    }
    
    public Categoria obtenerPorId(int id) {
        return categoriaDAO.obtenerPorId(id);
    }
    
    public boolean actualizar(Categoria categoria) {
        return categoriaDAO.actualizar(categoria);
    }
    
    public boolean eliminar(int id) {
        return categoriaDAO.eliminar(id);
    }
    
    public boolean asignarCategoriaAVideojuego(int videojuegoId, int categoriaId) {
        return categoriaDAO.asignarCategoriaAVideojuego(videojuegoId, categoriaId);
    }
    
    public List<Categoria> obtenerCategoriasPorVideojuego(int videojuegoId) {
        return categoriaDAO.obtenerCategoriasPorVideojuego(videojuegoId);
    }
}