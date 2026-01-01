package com.vacas.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.vacas.dao.BibliotecaDAO;
import com.vacas.dao.TransaccionDAO;
import com.vacas.dao.UsuarioDAO;
import com.vacas.dao.VideojuegoDAO;
import com.vacas.model.Transaccion;
import com.vacas.model.Usuario;
import com.vacas.model.Videojuego;
import com.vacas.utils.DatabaseConnection;

public class TransaccionService {
    private TransaccionDAO transaccionDAO;
    private UsuarioDAO usuarioDAO;
    private VideojuegoDAO videojuegoDAO;
    private BibliotecaDAO bibliotecaDAO;
    
    public TransaccionService() {
        this.transaccionDAO = new TransaccionDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.videojuegoDAO = new VideojuegoDAO();
        this.bibliotecaDAO = new BibliotecaDAO();
    }
    
    //  MÉTODO CORREGIDO: Ahora acepta fechaCompra como parámetro
    public boolean comprarVideojuego(int usuarioId, int videojuegoId, Date fechaCompra) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción
            
            // 1. Obtener datos (dentro de la transacción)
            Usuario usuario = usuarioDAO.obtenerPorId(usuarioId, conn);
            Videojuego videojuego = videojuegoDAO.obtenerPorId(videojuegoId, conn);
            
            if (usuario == null || videojuego == null || !videojuego.isDisponible()) {
                conn.rollback();
                return false;
            }
            
            // 2. Validar edad
            if (!validarEdad(usuario, videojuego, fechaCompra)) { // Pasamos fechaCompra
                conn.rollback();
                return false;
            }
            
            // 3. Verificar si ya tiene el juego
            if (bibliotecaDAO.existe(usuarioId, videojuegoId, conn)) {
                conn.rollback();
                return false; // Ya lo tiene
            }
            
            // 4. Verificar saldo suficiente
            if (usuario.getSaldoCartera() < videojuego.getPrecio()) {
                conn.rollback();
                return false;
            }
            
            // 5. Calcular comisión
            double comisionPorcentaje = 15.0; // Deberías obtener esto de ComisionDAO
            double montoComision = videojuego.getPrecio() * (comisionPorcentaje / 100);
            
            // 6. Crear objeto transacción CON LA FECHA DEL PARÁMETRO
            Transaccion transaccion = new Transaccion();
            transaccion.setUsuarioId(usuarioId);
            transaccion.setVideojuegoId(videojuegoId);
            transaccion.setFechaCompra(fechaCompra); // ✅ Usa fecha del parámetro
            transaccion.setPrecioPagado(videojuego.getPrecio());
            transaccion.setMontoComision(montoComision);
            transaccion.setTipoComision("GLOBAL");
            
            // 7. EJECUTAR TODAS LAS OPERACIONES (ATÓMICAS)
            // 7a. Descontar saldo
            if (!usuarioDAO.descontarSaldo(usuarioId, videojuego.getPrecio(), conn)) {
                conn.rollback();
                return false;
            }
            
            // 7b. Agregar a biblioteca
            if (!bibliotecaDAO.agregar(usuarioId, videojuegoId, conn)) {
                conn.rollback();
                return false;
            }
            
            // 7c. Registrar transacción
            if (!transaccionDAO.crear(transaccion, conn)) {
                conn.rollback();
                return false;
            }
            
            // 8. TODO OK - Hacer commit
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            // Rollback en caso de error
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
            e.printStackTrace();
            return false;
        } finally {
            // Cerrar conexión
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) {}
            }
        }
    }
    
    // MÉTODO AUXILIAR MEJORADO: Ahora recibe fechaCompra para validación precisa
    private boolean validarEdad(Usuario usuario, Videojuego videojuego, Date fechaCompra) {
        if (usuario.getFechaNacimiento() == null || fechaCompra == null) return false;
        
        // Calcular edad en el momento de la compra (no la fecha actual)
        long diffInMillis = fechaCompra.getTime() - usuario.getFechaNacimiento().getTime();
        long edad = diffInMillis / (1000L * 60 * 60 * 24 * 365);
        
        return edad >= videojuego.getEdadMinima();
    }
    
    // Métodos que NO necesitan transacción (solo consultas)
    public List<Transaccion> obtenerHistorialUsuario(int usuarioId) {
        return transaccionDAO.obtenerPorUsuario(usuarioId);
    }
    
    public double obtenerTotalComisiones() {
        return transaccionDAO.obtenerTotalComisiones();
    }
}