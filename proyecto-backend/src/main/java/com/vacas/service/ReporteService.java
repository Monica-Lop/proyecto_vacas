package com.vacas.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vacas.dao.ReporteDAO;

public class ReporteService {
    private ReporteDAO reporteDAO = new ReporteDAO();
    
    public List<Map<String, Object>> obtenerReporteGananciasGlobales() {
        return reporteDAO.obtenerReporteGananciasGlobales();
    }
    
    public List<Map<String, Object>> obtenerTopVideojuegos(int limite) {
        return reporteDAO.obtenerTopVideojuegos(limite);
    }
    
    public List<Map<String, Object>> obtenerReporteIngresosPorEmpresa() {
        return reporteDAO.obtenerReporteIngresosPorEmpresa();
    }
    
    public List<Map<String, Object>> obtenerReporteVentasPorEmpresa(int empresaId, Date fechaInicio, Date fechaFin) {
        return reporteDAO.obtenerReporteVentasPorEmpresa(empresaId, fechaInicio, fechaFin);
    }
    
    public List<Map<String, Object>> obtenerRankingUsuarios() {
        return reporteDAO.obtenerRankingUsuarios();
    }
    
    public List<Map<String, Object>> obtenerVentasPorMes(int año) {
        return reporteDAO.obtenerVentasPorMes(año);
    }
}