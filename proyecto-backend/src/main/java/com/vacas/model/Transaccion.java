package com.vacas.model;

import java.util.Date;

public class Transaccion {
    private int id;
    private int usuarioId;
    private int videojuegoId;
    private Date fechaCompra;
    private double precioPagado;
    private double montoComision;
    private String tipoComision;
    private Date fechaRegistro;
    
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
    public void setPrecioPagado(double precioPagado) { this.precioPagado = precioPagado; }
    
    public double getMontoComision() { return montoComision; }
    public void setMontoComision(double montoComision) { this.montoComision = montoComision; }
    
    public String getTipoComision() { return tipoComision; }
    public void setTipoComision(String tipoComision) { this.tipoComision = tipoComision; }
    
    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}