package com.vacas.model;

import java.util.Date;

public class ReporteVentasEmpresa {
    private Date fecha;
    private int cantidadVentas;
    private double totalVentas;
    private int idEmpresa;
    private String nombreEmpresa;
    
    public ReporteVentasEmpresa() {}
    
    public ReporteVentasEmpresa(Date fecha, int cantidadVentas, double totalVentas, 
                                int idEmpresa, String nombreEmpresa) {
        this.fecha = fecha;
        this.cantidadVentas = cantidadVentas;
        this.totalVentas = totalVentas;
        this.idEmpresa = idEmpresa;
        this.nombreEmpresa = nombreEmpresa;
    }
    
    // Getters y Setters
    public Date getFecha() {
        return fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public int getCantidadVentas() {
        return cantidadVentas;
    }
    
    public void setCantidadVentas(int cantidadVentas) {
        this.cantidadVentas = cantidadVentas;
    }
    
    public double getTotalVentas() {
        return totalVentas;
    }
    
    public void setTotalVentas(double totalVentas) {
        this.totalVentas = totalVentas;
    }
    
    public int getIdEmpresa() {
        return idEmpresa;
    }
    
    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }
    
    public String getNombreEmpresa() {
        return nombreEmpresa;
    }
    
    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }
}