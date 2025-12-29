package com.vacas.model;


import java.util.Date;

public class Videojuego {
    private int id;
    private String titulo;
    private String descripcion;
    private double precio;
    private int edadMinima;
    private String requisitos;
    private boolean disponible;
    private Date fechaLanzamiento;
    private int empresaId;
    
    // Constructores
    public Videojuego() {}
    
    public Videojuego(String titulo, String descripcion, double precio, int empresaId) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.empresaId = empresaId;
        this.disponible = true;
        this.edadMinima = 0;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    
    public int getEdadMinima() { return edadMinima; }
    public void setEdadMinima(int edadMinima) { this.edadMinima = edadMinima; }
    
    public String getRequisitos() { return requisitos; }
    public void setRequisitos(String requisitos) { this.requisitos = requisitos; }
    
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    
    public Date getFechaLanzamiento() { return fechaLanzamiento; }
    public void setFechaLanzamiento(Date fechaLanzamiento) { this.fechaLanzamiento = fechaLanzamiento; }
    
    public int getEmpresaId() { return empresaId; }
    public void setEmpresaId(int empresaId) { this.empresaId = empresaId; }
}