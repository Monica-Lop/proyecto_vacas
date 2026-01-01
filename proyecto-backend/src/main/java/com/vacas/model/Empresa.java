package com.vacas.model;

import java.util.Date;

public class Empresa {
    private int id;
    private String nombre;
    private String descripcion;
    private String telefono;
    private String politicaComentarios;
    private Date fechaCreacion;
    
    public Empresa() {}
    
    public Empresa(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getPoliticaComentarios() { return politicaComentarios; }
    public void setPoliticaComentarios(String politicaComentarios) { this.politicaComentarios = politicaComentarios; }
    
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}