package com.vacas.model;

import java.util.Date;

public class Calificacion {
    private int id;
    private int usuarioId;
    private int videojuegoId;
    private int estrellas;
    private Date fechaCalificacion;
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public int getVideojuegoId() { return videojuegoId; }
    public void setVideojuegoId(int videojuegoId) { this.videojuegoId = videojuegoId; }
    
    public int getEstrellas() { return estrellas; }
    public void setEstrellas(int estrellas) { this.estrellas = estrellas; }
    
    public Date getFechaCalificacion() { return fechaCalificacion; }
    public void setFechaCalificacion(Date fechaCalificacion) { this.fechaCalificacion = fechaCalificacion; }
}