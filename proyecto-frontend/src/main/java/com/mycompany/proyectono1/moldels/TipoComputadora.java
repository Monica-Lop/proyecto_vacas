/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author monicahernandez
 */
public class TipoComputadore implements Serializable {
    
    private int id;
    private String nombre;
    private String descripcion;
    private double precioVenta;  
    private List<RequisitoComponente> requisitos;
    
    public TipoComputadora() {
        this.requisitos = new ArrayList<>();
    }
    
    public TipoComputadora(int id, String nombre, String descripcion, double precioVenta) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
        this.requisitos = new ArrayList<>();
    }
    
    // Getters y Setters
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
    
    public double getPrecioVenta() {
        return precioVenta;  // Cambio de BigDecimal a double
    }
    
    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;  // Cambio de BigDecimal a double
    }
    
    public List<RequisitoComponente> getRequisitos() {
        return requisitos;
    }
    
    public void setRequisitos(List<RequisitoComponente> requisitos) {
        this.requisitos = requisitos;
    }
    
    /**
     * Agrega un requisito de componente
     * @param requisito El requisito a agregar
     */
    public void addRequisito(RequisitoComponente requisito) {
        if (this.requisitos == null) {
            this.requisitos = new ArrayList<>();
        }
        this.requisitos.add(requisito);
    }
    
    @Override
    public String toString() {
        return "TipoComputadora{" + "id=" + id + ", nombre=" + nombre + 
               ", descripcion=" + descripcion + ", precioVenta=" + precioVenta + 
               ", requisitos=" + requisitos.size() + '}';
    }
}
