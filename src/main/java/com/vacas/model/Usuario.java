package com.vacas.model;

public class Usuario {
    private int id;
    private String correo;
    private String password;
    private String nickname;
    private String fechaNacimiento; // Simple: String
    private String telefono;
    private String pais;
    private String tipo; // "ADMIN", "EMPRESA", "USUARIO"
    private boolean activo;
    private Integer empresaId;
    
    // Constructor vacío (OBLIGATORIO para JavaBeans)
    public Usuario() {}
    
    // Constructor para crear rápido
    public Usuario(String correo, String password, String nickname) {
        this.correo = correo;
        this.password = password;
        this.nickname = nickname;
        this.tipo = "USUARIO"; // Por defecto
        this.activo = true;
    }
    
    // GETTERS y SETTERS (genera con tu IDE o copia)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    public Integer getEmpresaId() { return empresaId; }
    public void setEmpresaId(Integer empresaId) { this.empresaId = empresaId; }
    public String toJsonSafe() {
        return String.format(
            "{\"id\":%d,\"correo\":\"%s\",\"nickname\":\"%s\",\"tipo\":\"%s\",\"empresaId\":%s}",
            id, correo, nickname, tipo, (empresaId != null ? empresaId.toString() : "null")
        );
    }
}


