package com.vacas.service;

import java.util.Date;
import java.util.List;

import com.vacas.dao.TransaccionDAO;
import com.vacas.dao.UsuarioDAO;
import com.vacas.dao.VideojuegoDAO;
import com.vacas.model.Transaccion;
import com.vacas.model.Usuario;
import com.vacas.model.Videojuego;

public class TransaccionService {
    private TransaccionDAO transaccionDAO;
    private UsuarioDAO usuarioDAO;
    private VideojuegoDAO videojuegoDAO;
    
    public TransaccionService() {
        this.transaccionDAO = new TransaccionDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.videojuegoDAO = new VideojuegoDAO();
    }
    
    public boolean comprarVideojuego(int usuarioId, int videojuegoId, Date fechaCompra) {
        Usuario usuario = obtenerUsuarioPorId(usuarioId);
        Videojuego videojuego = videojuegoDAO.obtenerPorId(videojuegoId);
        
        if (usuario == null || videojuego == null || !videojuego.isDisponible()) {
            return false;
        }
        
        // Verificar edad
        if (!validarEdad(usuario, videojuego)) {
            return false;
        }
        
        // Verificar saldo
        if (usuario.getSaldoCartera() < videojuego.getPrecio()) {
            return false;
        }
        
        // Calcular comisión (15% por defecto)
        double comisionPorcentaje = 15.0;
        double montoComision = videojuego.getPrecio() * (comisionPorcentaje / 100);
        double precioNeto = videojuego.getPrecio() - montoComision;
        
        // Crear transacción
        Transaccion transaccion = new Transaccion();
        transaccion.setUsuarioId(usuarioId);
        transaccion.setVideojuegoId(videojuegoId);
        transaccion.setFechaCompra(fechaCompra);
        transaccion.setPrecioPagado(videojuego.getPrecio());
        transaccion.setMontoComision(montoComision);
        transaccion.setTipoComision("GLOBAL");
        
        // Actualizar saldo del usuario
        double nuevoSaldo = usuario.getSaldoCartera() - videojuego.getPrecio();
        usuarioDAO.actualizarSaldo(usuarioId, nuevoSaldo);
        
        // Registrar transacción
        return transaccionDAO.crear(transaccion);
    }
    
    public List<Transaccion> obtenerHistorialUsuario(int usuarioId) {
        return transaccionDAO.obtenerPorUsuario(usuarioId);
    }
    
    public double obtenerTotalComisiones() {
        return transaccionDAO.obtenerTotalComisiones();
    }
    
    private boolean validarEdad(Usuario usuario, Videojuego videojuego) {
        // Calcular edad del usuario
        Date hoy = new Date();
        long diff = hoy.getTime() - usuario.getFechaNacimiento().getTime();
        long edad = diff / (1000L * 60 * 60 * 24 * 365);
        
        return edad >= videojuego.getEdadMinima();
    }
    
    private Usuario obtenerUsuarioPorId(int id) {
        // Este método sería implementado en UsuarioDAO
        return null;
    }
}