package com.vacas.model;

public class ReporteIngresosEmpresa {
    private int idEmpresa;
    private String nombreEmpresa;
    private double totalIngresos;
    
    public ReporteIngresosEmpresa() {}
    
    public ReporteIngresosEmpresa(int idEmpresa, String nombreEmpresa, double totalIngresos) {
        this.idEmpresa = idEmpresa;
        this.nombreEmpresa = nombreEmpresa;
        this.totalIngresos = totalIngresos;
    }
    
    // Getters y Setters
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
    
    public double getTotalIngresos() {
        return totalIngresos;
    }
    
    public void setTotalIngresos(double totalIngresos) {
        this.totalIngresos = totalIngresos;
    }
}