package com.vacas.model;

import java.util.Date;

public class Comentario {
    private int id;
    private int usuarioId;
    private int videojuegoId;
    private String texto;
    private Date fechaComentario;
    private boolean visible;
    private Integer comentarioPadreId;
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public int getVideojuegoId() { return videojuegoId; }
    public void setVideojuegoId(int videojuegoId) { this.videojuegoId = videojuegoId; }
    
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    
    public Date getFechaComentario() { return fechaComentario; }
    public void setFechaComentario(Date fechaComentario) { this.fechaComentario = fechaComentario; }
    
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    
    public Integer getComentarioPadreId() { return comentarioPadreId; }
    public void setComentarioPadreId(Integer comentarioPadreId) { this.comentarioPadreId = comentarioPadreId; }
}