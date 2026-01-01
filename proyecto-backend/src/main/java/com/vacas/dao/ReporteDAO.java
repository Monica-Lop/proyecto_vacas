package com.vacas.dao;

import java.util.Date;
import java.util.List;

import com.vacas.model.ReporteIngresosEmpresa;
import com.vacas.model.ReporteRankingUsuarios;
import com.vacas.model.ReporteVentasEmpresa;
import com.vacas.model.Transaccion;
import com.vacas.model.Videojuego;

public interface ReporteDAO {
    // Métodos existentes
    List<Transaccion> obtenerTransaccionesPorUsuario(int usuarioId);
    List<Transaccion> obtenerTransaccionesPorVideojuego(int videojuegoId);
    List<Transaccion> obtenerTransaccionesPorEmpresa(int empresaId);
    
    // Métodos nuevos para los reportes
    double obtenerReporteGananciasGlobales();
    List<Videojuego> obtenerTopVideojuegos(int topN);
    List<ReporteIngresosEmpresa> obtenerReporteIngresosPorEmpresa();
    List<ReporteVentasEmpresa> obtenerReporteVentasPorEmpresa(int idEmpresa, Date fechaInicio, Date fechaFin);
    List<ReporteRankingUsuarios> obtenerRankingUsuarios();
}