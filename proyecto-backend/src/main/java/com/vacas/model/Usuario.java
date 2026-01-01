package com.vacas.model;

import java.util.Date;

public class Usuario {
    private int id;
    private String correo;
    private String password;
    private String nickname;
    private Date fechaNacimiento;
    private String telefono;
    private String pais;
    private byte[] avatar;
    private String tipoUsuario;
    private boolean activo;
    private boolean bibliotecaPublica;
    private double saldoCartera;
    private Integer empresaId;
    private Date fechaCreacion;
    
    public Usuario() {}
    
    public Usuario(int id, String correo, String nickname, String tipoUsuario) {
        this.id = id;
        this.correo = correo;
        this.nickname = nickname;
        this.tipoUsuario = tipoUsuario;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    
    public byte[] getAvatar() { return avatar; }
    public void setAvatar(byte[] avatar) { this.avatar = avatar; }
    
    public String getTipo() { return tipoUsuario; }
    public void setTipo(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    public boolean isBibliotecaPublica() { return bibliotecaPublica; }
    public void setBibliotecaPublica(boolean bibliotecaPublica) { this.bibliotecaPublica = bibliotecaPublica; }
    
    public double getSaldoCartera() { return saldoCartera; }
    public void setSaldoCartera(double saldoCartera) { this.saldoCartera = saldoCartera; }
    
    public Integer getEmpresaId() { return empresaId; }
    public void setEmpresaId(Integer empresaId) { this.empresaId = empresaId; }
    
    public Date getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Date fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}