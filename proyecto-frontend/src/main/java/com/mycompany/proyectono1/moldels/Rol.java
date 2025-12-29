/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import java.io.Serializable;

/**
 *
 * @author monicahernandez
 */
public class Rol implements Serializable {
    
    private int id;
    private String nombre;
    private String descripcion;
    
    public Rol() {
    }
    
    public Rol(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    @Override
    public String toString() {
        return "Rol{" + "id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + '}';
    }
}
    
