/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author monicahernandez
 */
public class Componente implements Serializable {
    
    private int id;
    private String tipo;
    private String nombre;
    private double costo; 
    private int cantidadDisponible;
    private int cantidadMinima;
    private Date fechaIngreso;
    
    public Componente() {
    }
    
    public Componente(int id, String tipo, String nombre, double costo, 
                      int cantidadDisponible, int cantidadMinima, Date fechaIngreso) {
        this.id = id;
        this.tipo = tipo;
        this.nombre = nombre;
        this.costo = costo;
        this.cantidadDisponible = cantidadDisponible;
        this.cantidadMinima = cantidadMinima;
        this.fechaIngreso = fechaIngreso;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public double getCosto() {
        return costo;
    }
    
    public void setCosto(double costo) {
        this.costo = costo;
    }
    
    public int getCantidadDisponible() {
        return cantidadDisponible;
    }
    
    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }
    
    public int getCantidadMinima() {
        return cantidadMinima;
    }
    
    public void setCantidadMinima(int cantidadMinima) {
        this.cantidadMinima = cantidadMinima;
    }
    
    public Date getFechaIngreso() {
        return fechaIngreso;
    }
    
    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
    
    /**
     * Verifica si la cantidad disponible está por debajo del mínimo
     * @return true si la cantidad es menor que la mínima
     */
    public boolean isStockBajo() {
        return cantidadDisponible < cantidadMinima;
    }
    
    @Override
    public String toString() {
        return "Componente{" + "id=" + id + ", tipo=" + tipo + ", nombre=" + nombre + 
               ", costo=" + costo + ", cantidadDisponible=" + cantidadDisponible + 
               ", cantidadMinima=" + cantidadMinima + ", fechaIngreso=" + fechaIngreso + '}';
    }
}
