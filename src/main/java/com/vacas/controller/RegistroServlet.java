package com.vacas.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vacas.dao.UsuarioDAO;
import com.vacas.model.Usuario;
import com.vacas.util.ConexionMySQL;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/registro")
public class RegistroServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Mostrar formulario de registro
        request.getRequestDispatcher("/registro.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Obtener parámetros del formulario
        String nickname = request.getParameter("nickname");
        String correo = request.getParameter("correo");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fechaNacimientoStr = request.getParameter("fechaNacimiento");
        String telefono = request.getParameter("telefono");
        String pais = request.getParameter("pais");
        
        // Validaciones básicas
        if (nickname == null || nickname.trim().isEmpty() ||
            correo == null || correo.trim().isEmpty() ||
            password == null || password.trim().isEmpty() ||
            fechaNacimientoStr == null || fechaNacimientoStr.trim().isEmpty() ||
            telefono == null || telefono.trim().isEmpty() ||
            pais == null || pais.trim().isEmpty()) {
            
            request.setAttribute("error", "Todos los campos son obligatorios");
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }
        
        // Validar que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Las contraseñas no coinciden");
            request.setAttribute("nickname", nickname);
            request.setAttribute("correo", correo);
            request.setAttribute("telefono", telefono);
            request.setAttribute("pais", pais);
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }
        
        // Validar longitud mínima de contraseña
        if (password.length() < 6) {
            request.setAttribute("error", "La contraseña debe tener al menos 6 caracteres");
            request.setAttribute("nickname", nickname);
            request.setAttribute("correo", correo);
            request.setAttribute("telefono", telefono);
            request.setAttribute("pais", pais);
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }
        
        // Validar formato de teléfono (8 dígitos)
        if (!telefono.matches("\\d{8}")) {
            request.setAttribute("error", "El teléfono debe tener 8 dígitos");
            request.setAttribute("nickname", nickname);
            request.setAttribute("correo", correo);
            request.setAttribute("telefono", telefono);
            request.setAttribute("pais", pais);
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
            return;
        }
        
        // Validar edad mínima (13 años)
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaNacimiento = sdf.parse(fechaNacimientoStr);
            Date hoy = new Date();
            
            // Calcular edad
            long diff = hoy.getTime() - fechaNacimiento.getTime();
            long edad = diff / (1000L * 60 * 60 * 24 * 365);
            
            if (edad < 13) {
                request.setAttribute("error", "Debes tener al menos 13 años para registrarte");
                request.setAttribute("nickname", nickname);
                request.setAttribute("correo", correo);
                request.setAttribute("telefono", telefono);
                request.setAttribute("pais", pais);
                request.getRequestDispatcher("/registro.jsp").forward(request, response);
                return;
            }
            
            // Crear objeto Usuario
            Usuario usuario = new Usuario();
            usuario.setCorreo(correo);
            usuario.setPassword(password); // Temporalmente en texto plano
            usuario.setNickname(nickname);
            usuario.setFechaNacimiento(fechaNacimiento);
            usuario.setTelefono(telefono);
            usuario.setPais(pais);
            usuario.setTipoUsuario(Usuario.TipoUsuario.USUARIO_COMUN);
            usuario.setActivo(true);
            usuario.setBibliotecaPublica(true); // Por defecto la biblioteca es pública
            
            // Verificar si el correo ya existe
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario existente = usuarioDAO.buscarPorCorreo(correo);
            
            if (existente != null) {
                request.setAttribute("error", "El correo electrónico ya está registrado");
                request.setAttribute("nickname", nickname);
                request.setAttribute("telefono", telefono);
                request.setAttribute("pais", pais);
                request.getRequestDispatcher("/registro.jsp").forward(request, response);
                return;
            }
            
            // Crear usuario en la base de datos
            boolean creado = usuarioDAO.crearUsuario(usuario);
            
            if (creado) {
                // Crear cartera digital con saldo inicial 0.00
                crearCartera(usuario.getId());
                
                // Redirigir al login con mensaje de éxito
                response.sendRedirect("login.jsp?success=Cuenta creada exitosamente. Ahora puedes iniciar sesión.");
            } else {
                request.setAttribute("error", "Error al crear la cuenta. Intenta nuevamente.");
                request.getRequestDispatcher("/registro.jsp").forward(request, response);
            }
            
        } catch (ParseException e) {
            request.setAttribute("error", "Fecha de nacimiento inválida");
            request.setAttribute("nickname", nickname);
            request.setAttribute("correo", correo);
            request.setAttribute("telefono", telefono);
            request.setAttribute("pais", pais);
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error en el sistema: " + e.getMessage());
            request.getRequestDispatcher("/registro.jsp").forward(request, response);
        }
    }
    
    /**
     * Crea una cartera digital para el usuario con saldo inicial 0.00
     */
    private void crearCartera(int usuarioId) {
        Connection conexion = null;
        PreparedStatement stmt = null;
        
        try {
            conexion = ConexionMySQL.obtenerConexion();
            String sql = "INSERT INTO cartera (usuario_id, saldo) VALUES (?, 0.00) " +
                        "ON DUPLICATE KEY UPDATE saldo = 0.00";
            
            stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();
            
            System.out.println("✅ Cartera creada para usuario ID: " + usuarioId);
            
        } catch (SQLException e) {
            System.err.println("❌ Error al crear cartera: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}