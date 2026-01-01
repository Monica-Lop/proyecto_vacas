package com.vacas.service;

import java.util.List;

import com.vacas.dao.ComisionDAO;
import com.vacas.model.ComisionEspecial;
import com.vacas.model.ComisionGlobal;

public class ComisionService {
    private ComisionDAO comisionDAO;
    
    public ComisionService() {
        this.comisionDAO = new ComisionDAO();
    }
    
    public ComisionGlobal obtenerComisionGlobalActual() {
        return comisionDAO.obtenerComisionGlobalActual();
    }
    
    public boolean establecerComisionGlobal(ComisionGlobal comision) {
        return comisionDAO.crearComisionGlobal(comision);
    }
    
    public ComisionEspecial obtenerComisionEspecial(int empresaId) {
        return comisionDAO.obtenerComisionEspecial(empresaId);
    }
    
    public boolean establecerComisionEspecial(ComisionEspecial comision) {
        // Verificar que no sea mayor a la comisión global
        ComisionGlobal global = obtenerComisionGlobalActual();
        if (global != null && comision.getPorcentaje() > global.getPorcentaje()) {
            return false;
        }
        
        return comisionDAO.crearComisionEspecial(comision);
    }
    
    public boolean eliminarComisionEspecial(int empresaId) {
        return comisionDAO.eliminarComisionEspecial(empresaId);
    }
    
    public List<ComisionEspecial> obtenerTodasComisionesEspeciales() {
        return comisionDAO.obtenerTodasComisionesEspeciales();
    }
    
    public void ajustarComisionesEspeciales(double nuevaComisionGlobal) {
        List<ComisionEspecial> comisiones = obtenerTodasComisionesEspeciales();
        
        for (ComisionEspecial comision : comisiones) {
            if (comision.getPorcentaje() > nuevaComisionGlobal) {
                // Ajustar al nuevo valor global
                comision.setPorcentaje(nuevaComisionGlobal);
                comisionDAO.crearComisionEspecial(comision);
            }
        }
    }
}