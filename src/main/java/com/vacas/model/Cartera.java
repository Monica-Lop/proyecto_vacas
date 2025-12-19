package com.vacas.model;


public class Cartera {
    private int usuarioId;
    private double saldo;
    
    public Cartera() {}
    
    public Cartera(int usuarioId, double saldo) {
        this.usuarioId = usuarioId;
        this.saldo = saldo;
    }
    
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}