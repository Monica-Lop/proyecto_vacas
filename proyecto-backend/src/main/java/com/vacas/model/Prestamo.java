package com.vacas.model;

import java.util.Date;

public class Prestamo {
    private int id;
    private int usuarioId;
    private int videojuegoId;
    private String estado; // INSTALADO, NO_INSTALADO
    private Date fechaInstalacion;
    private Date fechaDesinstalacion;
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public int getVideojuegoId() { return videojuegoId; }
    public void setVideojuegoId(int videojuegoId) { this.videojuegoId = videojuegoId; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public Date getFechaInstalacion() { return fechaInstalacion; }
    public void setFechaInstalacion(Date fechaInstalacion) { this.fechaInstalacion = fechaInstalacion; }
    
    public Date getFechaDesinstalacion() { return fechaDesinstalacion; }
    public void setFechaDesinstalacion(Date fechaDesinstalacion) { this.fechaDesinstalacion = fechaDesinstalacion; }
}