package com.vacas.service;

import java.util.List;

import com.vacas.dao.EmpresaDAO;
import com.vacas.model.Empresa;

public class EmpresaService {
    private EmpresaDAO empresaDAO;
    
    public EmpresaService() {
        this.empresaDAO = new EmpresaDAO();
    }
    
    // Lógica para crear empresa 
    public boolean crearEmpresa(Empresa empresa) {
        if (empresa.getNombre() == null || empresa.getNombre().trim().isEmpty()) {
            System.out.println("El nombre de la empresa es requerido");
            return false;
        }
        
        if (empresa.getComision() < 0 || empresa.getComision() > 100) {
            System.out.println("La comisión debe estar entre 0 y 100");
            return false;
        }
        
        return empresaDAO.crear(empresa);
    }
    
    public List<Empresa> listarEmpresas() {
        return empresaDAO.listarTodas();
    }
    
    public Empresa obtenerEmpresaPorId(int id) {
        return empresaDAO.buscarPorId(id);
    }
    
    public boolean actualizarEmpresa(Empresa empresa) {
        return empresaDAO.actualizar(empresa);
    }
    
    public boolean eliminarEmpresa(int id) {
        return empresaDAO.eliminar(id);
    }
}