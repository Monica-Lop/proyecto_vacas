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
public class RequisitoComponente implements Serializable {

    private int idTipoComputadora;
    private int idComponente;
    private int cantidadRequerida;
    
    // Campos auxiliares para mostrar información adicional
    private String nombreComponente;
    private String tipoComponente;
    
    public RequisitoComponente() {
    }
    
    public RequisitoComponente(int idTipoComputadora, int idComponente, int cantidadRequerida) {
        this.idTipoComputadora = idTipoComputadora;
        this.idComponente = idComponente;
        this.cantidadRequerida = cantidadRequerida;
    }
    
    // Getters y Setters
    public int getIdTipoComputadora() {
        return idTipoComputadora;
    }
    
    public void setIdTipoComputadora(int idTipoComputadora) {
        this.idTipoComputadora = idTipoComputadora;
    }
    
    public int getIdComponente() {
        return idComponente;
    }
    
    public void setIdComponente(int idComponente) {
        this.idComponente = idComponente;
    }
    
    public int getCantidadRequerida() {
        return cantidadRequerida;
    }
    
    public void setCantidadRequerida(int cantidadRequerida) {
        this.cantidadRequerida = cantidadRequerida;
    }
    
    public String getNombreComponente() {
        return nombreComponente;
    }
    
    public void setNombreComponente(String nombreComponente) {
        this.nombreComponente = nombreComponente;
    }
    
    public String getTipoComponente() {
        return tipoComponente;
    }
    
    public void setTipoComponente(String tipoComponente) {
        this.tipoComponente = tipoComponente;
    }
    
    @Override
    public String toString() {
        return "RequisitoComponente{" + "idTipoComputadora=" + idTipoComputadora + 
               ", idComponente=" + idComponente + ", cantidadRequerida=" + cantidadRequerida + 
               ", nombreComponente=" + nombreComponente + ", tipoComponente=" + tipoComponente + '}';
    }
}
    
