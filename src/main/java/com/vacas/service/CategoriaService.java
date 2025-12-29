package com.vacas.service;

import java.util.List;

import com.vacas.dao.CategoriaDAO;
import com.vacas.model.Categoria;

public class CategoriaService {
    private CategoriaDAO categoriaDAO;
    
    public CategoriaService() {
        this.categoriaDAO = new CategoriaDAO();
    }
    
    // Crear categoría con validaciones
    public boolean crearCategoria(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            System.out.println(" Nombre de categoría requerido");
            return false;
        }
        
        Categoria existente = categoriaDAO.buscarPorNombre(categoria.getNombre());
        if (existente != null) {
            System.out.println("Ya existe una categoría con ese nombre");
            return false;
        }
        
        return categoriaDAO.crear(categoria);
    }
    
    public List<Categoria> listarCategorias() {
        return categoriaDAO.listarTodas();
    }
    
    public Categoria obtenerCategoria(int id) {
        return categoriaDAO.buscarPorId(id);
    }
    
    public boolean actualizarCategoria(Categoria categoria) {
        if (categoria.getNombre() == null || categoria.getNombre().trim().isEmpty()) {
            return false;
        }
        
        return categoriaDAO.actualizar(categoria);
    }
    
    public boolean eliminarCategoria(int id) {
        return categoriaDAO.eliminar(id);
    }
    
    public boolean asignarCategoriaAVideojuego(int videojuegoId, int categoriaId) {
        return categoriaDAO.asignarCategoriaAVideojuego(videojuegoId, categoriaId);
    }
}