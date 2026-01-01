package com.vacas.service;

import com.vacas.dao.ReporteDAO;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReporteService {
    private ReporteDAO reporteDAO;
    
    public ReporteService() {
        this.reporteDAO = new ReporteDAO();
    }
    
    public List<Map<String, Object>> obtenerGananciasGlobales() {
        return reporteDAO.obtenerReporteGananciasGlobales();
    }
    
    public List<Map<String, Object>> obtenerTopVideojuegos(int limite) {
        return reporteDAO.obtenerTopVideojuegos(limite);
    }
    
    public List<Map<String, Object>> obtenerIngresosPorEmpresa() {
        return reporteDAO.obtenerReporteIngresosPorEmpresa();
    }
    
    public List<Map<String, Object>> obtenerVentasPorEmpresa(int empresaId, Date fechaInicio, Date fechaFin) {
        return reporteDAO.obtenerReporteVentasPorEmpresa(empresaId, fechaInicio, fechaFin);
    }
    
    public List<Map<String, Object>> obtenerRankingUsuarios() {
        return reporteDAO.obtenerRankingUsuarios();
    }
}