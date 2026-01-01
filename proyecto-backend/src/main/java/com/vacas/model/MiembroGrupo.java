package com.vacas.model;

import java.util.Date;

public class MiembroGrupo {
    private int id;
    private int grupoId;
    private int usuarioId;
    private String estado; // PENDIENTE, ACTIVO, RECHAZADO, EXPULSADO
    private Date fechaUnion;
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getGrupoId() { return grupoId; }
    public void setGrupoId(int grupoId) { this.grupoId = grupoId; }
    
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public Date getFechaUnion() { return fechaUnion; }
    public void setFechaUnion(Date fechaUnion) { this.fechaUnion = fechaUnion; }
}