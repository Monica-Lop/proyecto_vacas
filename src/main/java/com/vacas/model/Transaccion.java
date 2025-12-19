package com.vacas.model;

import java.util.Date;

public class Transaccion {
    private int id;
    private int usuarioId;
    private int videojuegoId;
    private Date fechaCompra;
    private double precioPagado;
    private double comisionAplicada; // porcentaje
    private double montoComision;    // monto en dinero
    private double montoEmpresa;     // monto que recibe la empresa
    
    // Constructores
    public Transaccion() {}
    
    public Transaccion(int usuarioId, int videojuegoId, double precioPagado, double comisionAplicada) {
        this.usuarioId = usuarioId;
        this.videojuegoId = videojuegoId;
        this.precioPagado = precioPagado;
        this.comisionAplicada = comisionAplicada;
        this.fechaCompra = new Date();
        calcularMontos();
    }
    
    private void calcularMontos() {
        this.montoComision = this.precioPagado * (this.comisionAplicada / 100);
        this.montoEmpresa = this.precioPagado - this.montoComision;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public int getVideojuegoId() { return videojuegoId; }
    public void setVideojuegoId(int videojuegoId) { this.videojuegoId = videojuegoId; }
    
    public Date getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(Date fechaCompra) { this.fechaCompra = fechaCompra; }
    
    public double getPrecioPagado() { return precioPagado; }
    public void setPrecioPagado(double precioPagado) { 
        this.precioPagado = precioPagado;
        calcularMontos();
    }
    
    public double getComisionAplicada() { return comisionAplicada; }
    public void setComisionAplicada(double comisionAplicada) { 
        this.comisionAplicada = comisionAplicada;
        calcularMontos();
    }
    
    public double getMontoComision() { return montoComision; }
    public double getMontoEmpresa() { return montoEmpresa; }
}