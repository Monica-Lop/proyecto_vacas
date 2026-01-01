package com.vacas.model;

import java.util.Date;

public class Biblioteca {
    private int id;
    private int usuarioId;
    private int videojuegoId;
    private Date fechaAgregado;
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public int getVideojuegoId() { return videojuegoId; }
    public void setVideojuegoId(int videojuegoId) { this.videojuegoId = videojuegoId; }
    
    public Date getFechaAgregado() { return fechaAgregado; }
    public void setFechaAgregado(Date fechaAgregado) { this.fechaAgregado = fechaAgregado; }
}