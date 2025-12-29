package com.vacas.service;

import java.util.Date;
import java.util.List;

import com.vacas.dao.*;
import com.vacas.model.*;

public class TransaccionService {
    private TransaccionDAO transaccionDAO;
    private CarteraDAO carteraDAO;
    private VideojuegoDAO videojuegoDAO;
    private UsuarioDAO usuarioDAO;
    private EmpresaDAO empresaDAO;
    
    public TransaccionService() {
        this.transaccionDAO = new TransaccionDAO();
        this.carteraDAO = new CarteraDAO();
        this.videojuegoDAO = new VideojuegoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.empresaDAO = new EmpresaDAO();
    }
    
    // REALIZAR COMPRA
    public Transaccion realizarCompra(int usuarioId, int videojuegoId) {
        System.out.println(" Intentando compra - Usuario: " + usuarioId + ", Videojuego: " + videojuegoId);
        
        Usuario usuario = usuarioDAO.buscarPorId(usuarioId);
        Videojuego videojuego = videojuegoDAO.buscarPorId(videojuegoId);
        
        if (usuario == null || videojuego == null) {
            System.out.println(" Usuario o videojuego no encontrado");
            return null;
        }
        
        if (!videojuego.isDisponible()) {
            System.out.println(" Videojuego no disponible");
            return null;
        }
        
        if (!validarEdad(usuario, videojuego)) {
            System.out.println(" Usuario no cumple edad mínima");
            return null;
        }
        
        Cartera cartera = carteraDAO.obtenerPorUsuario(usuarioId);
        if (cartera == null || cartera.getSaldo() < videojuego.getPrecio()) {
            System.out.println(" Saldo insuficiente");
            return null;
        }
        
        double comision = obtenerComision(videojuego.getEmpresaId());
        
        Transaccion transaccion = new Transaccion(
            usuarioId, 
            videojuegoId, 
            videojuego.getPrecio(), 
            comision
        );
        
        boolean saldoDescontado = carteraDAO.descontarSaldo(usuarioId, videojuego.getPrecio());
        if (!saldoDescontado) {
            System.out.println(" Error al descontar saldo");
            return null;
        }
        
        boolean transaccionCreada = transaccionDAO.crear(transaccion);
        if (!transaccionCreada) {
            System.out.println("Error al crear transacción");
            carteraDAO.recargarSaldo(usuarioId, videojuego.getPrecio());
            return null;
        }
        
        System.out.println("Compra exitosa - Transacción ID: " + transaccion.getId());
        return transaccion;
    }
    
    // VALIDAR EDAD
    private boolean validarEdad(Usuario usuario, Videojuego videojuego) {
        try {
            // Calcular edad .......
            String fechaNac = usuario.getFechaNacimiento();
            int añoNacimiento = Integer.parseInt(fechaNac.substring(0, 4));
            int añoActual = new Date().getYear() + 1900; //....
            int edad = añoActual - añoNacimiento;
            
            return edad >= videojuego.getEdadMinima();
        } catch (Exception e) {
            return false;
        }
    }
    
    // OBTENER COMISIÓN
    private double obtenerComision(int empresaId) {
        // ....
        return 15.0; 
    }
    
    // RECARGAR CARTERA
    public boolean recargarCartera(int usuarioId, double monto) {
        if (monto <= 0) {
            System.out.println(" Monto debe ser positivo");
            return false;
        }
        
        boolean recargado = carteraDAO.recargarSaldo(usuarioId, monto);
        if (recargado) {
            System.out.println("Recarga exitosa - Usuario: " + usuarioId + ", Monto: $" + monto);
        }
        return recargado;
    }
    
    // OBTENER HISTORIAL
    public List<Transaccion> obtenerHistorialUsuario(int usuarioId) {
        return transaccionDAO.listarPorUsuario(usuarioId);
    }
    
    // OBTENER SALDO
    public double obtenerSaldo(int usuarioId) {
        Cartera cartera = carteraDAO.obtenerPorUsuario(usuarioId);
        return cartera != null ? cartera.getSaldo() : 0.0;
    }
}