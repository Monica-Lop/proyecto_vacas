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
public class Usuario implements Serializable {
    
    private int id;
    private String nombreUsuario;
    private String contraseña;
    private int idRol;
    private String nombreRol; // Campo auxiliar para mostrar el nombre del rol
    
    public Usuario() {
    }
    
    public Usuario(int id, String nombreUsuario, String contraseña, int idRol) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.contraseña = contraseña;
        this.idRol = idRol;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombreUsuario() {
        return nombreUsuario;
    }
    
    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
    
    public String getContraseña() {
        return contraseña;
    }
    
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }
    
    public int getIdRol() {
        return idRol;
    }
    
    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }
    
    public String getNombreRol() {
        return nombreRol;
    }
    
    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
    
    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", nombreUsuario=" + nombreUsuario + 
               ", idRol=" + idRol + ", nombreRol=" + nombreRol + '}';
    }
}
    
