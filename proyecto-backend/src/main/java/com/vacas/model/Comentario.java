package com.vacas.model;

import java.util.Date;

public class Comentario {
    private int id;
    private int usuarioId;
    private int videojuegoId;
    private String texto;
    private int calificacion; 
    private Date fecha;
    private boolean visible;
    private Integer comentarioPadreId;
    
    // Constructores
    public Comentario() {}
    
    public Comentario(int usuarioId, int videojuegoId, String texto, int calificacion) {
        this.usuarioId = usuarioId;
        this.videojuegoId = videojuegoId;
        this.texto = texto;
        this.calificacion = calificacion;
        this.fecha = new Date();
        this.visible = true;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public int getVideojuegoId() { return videojuegoId; }
    public void setVideojuegoId(int videojuegoId) { this.videojuegoId = videojuegoId; }
    
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    
    public int getCalificacion() { return calificacion; }
    public void setCalificacion(int calificacion) { 
        if (calificacion < 1 || calificacion > 5) {
            throw new IllegalArgumentException("La calificación debe ser entre 1 y 5");
        }
        this.calificacion = calificacion; 
    }
    
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    
    public Integer getComentarioPadreId() { return comentarioPadreId; }
    public void setComentarioPadreId(Integer comentarioPadreId) { this.comentarioPadreId = comentarioPadreId; }
}