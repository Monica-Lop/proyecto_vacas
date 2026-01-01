package com.vacas.service;

import java.util.Date;
import java.util.List;

import com.vacas.dao.ReporteDAO;
import com.vacas.dao.ReporteDAOImpl;
import com.vacas.model.ReporteIngresosEmpresa;
import com.vacas.model.ReporteRankingUsuarios;
import com.vacas.model.ReporteVentasEmpresa;
import com.vacas.model.Videojuego;

public class ReporteService {
    
    private ReporteDAO reporteDAO;
    
    public ReporteService() {
        this.reporteDAO = new ReporteDAOImpl();
    }
    
    public double obtenerReporteGananciasGlobales() {
        return reporteDAO.obtenerReporteGananciasGlobales();
    }
    
    public List<Videojuego> obtenerTopVideojuegos(int topN) {
        return reporteDAO.obtenerTopVideojuegos(topN);
    }
    
    public List<ReporteIngresosEmpresa> obtenerReporteIngresosPorEmpresa() {
        return reporteDAO.obtenerReporteIngresosPorEmpresa();
    }
    
    public List<ReporteVentasEmpresa> obtenerReporteVentasPorEmpresa(int idEmpresa, Date fechaInicio, Date fechaFin) {
        return reporteDAO.obtenerReporteVentasPorEmpresa(idEmpresa, fechaInicio, fechaFin);
    }
    
    public List<ReporteRankingUsuarios> obtenerRankingUsuarios() {
        return reporteDAO.obtenerRankingUsuarios();
    }
}