package com.vacas.model;

public class ReporteRankingUsuarios {
    private int idUsuario;
    private String username;
    private String nombre;
    private int cantidadCompras;
    private double totalGastado;
    
    public ReporteRankingUsuarios() {}
    
    public ReporteRankingUsuarios(int idUsuario, String username, String nombre, 
                                  int cantidadCompras, double totalGastado) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.nombre = nombre;
        this.cantidadCompras = cantidadCompras;
        this.totalGastado = totalGastado;
    }
    
    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }
    
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public int getCantidadCompras() {
        return cantidadCompras;
    }
    
    public void setCantidadCompras(int cantidadCompras) {
        this.cantidadCompras = cantidadCompras;
    }
    
    public double getTotalGastado() {
        return totalGastado;
    }
    
    public void setTotalGastado(double totalGastado) {
        this.totalGastado = totalGastado;
    }
}