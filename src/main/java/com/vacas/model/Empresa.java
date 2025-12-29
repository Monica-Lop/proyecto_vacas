package com.vacas.model;

public class Empresa {
    private int id;
    private String nombre;
    private String descripcion;
    private String telefono;
    private double comision; 

    public Empresa(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.comision = 15.0; // 

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public double getComision() { return comision; }
    public void setComision(double comision) { this.comision = comision; }
}

    }