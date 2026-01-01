package com.vacas.service;

import java.util.List;

import com.vacas.dao.EmpresaDAO;
import com.vacas.model.Empresa;

public class EmpresaService {
    private EmpresaDAO empresaDAO = new EmpresaDAO();
    
    public boolean crear(Empresa empresa) {
        // Verificar si ya existe una empresa con ese nombre
        if (empresaDAO.existeNombre(empresa.getNombre())) {
            return false;
        }
        return empresaDAO.crear(empresa);
    }
    
    public List<Empresa> obtenerTodas() {
        return empresaDAO.obtenerTodas();
    }
    
    public Empresa obtenerPorId(int id) {
        return empresaDAO.obtenerPorId(id);
    }
    
    public boolean actualizar(Empresa empresa) {
        return empresaDAO.actualizar(empresa);
    }
    
    public boolean eliminar(int id) {
        return empresaDAO.eliminar(id);
    }
    
}
