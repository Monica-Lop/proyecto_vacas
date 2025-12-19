<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registro - Plataforma de Videojuegos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        .register-card {
            background: white;
            border-radius: 15px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            width: 100%;
            max-width: 500px;
            padding: 40px;
        }
        .logo {
            text-align: center;
            margin-bottom: 30px;
        }
        .logo h2 {
            color: #333;
            font-weight: bold;
            margin-bottom: 5px;
        }
        .logo p {
            color: #666;
            font-size: 14px;
        }
        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }
        .btn-success {
            background: linear-gradient(135deg, #4CAF50 0%, #2E7D32 100%);
            border: none;
            width: 100%;
            padding: 12px;
            font-weight: 600;
        }
        .btn-success:hover {
            opacity: 0.9;
        }
        .login-link {
            text-align: center;
            margin-top: 20px;
            color: #666;
        }
        .login-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
        }
        .login-link a:hover {
            text-decoration: underline;
        }
        .password-strength {
            height: 5px;
            margin-top: 5px;
            border-radius: 2px;
        }
        .weak { background-color: #ff4757; }
        .medium { background-color: #ffa502; }
        .strong { background-color: #2ed573; }
    </style>
</head>
<body>
    <div class="register-card">
        <div class="logo">
            <h2>🎮 Regístrate en Vaqueras</h2>
            <p>Únete a nuestra comunidad de videojuegos</p>
        </div>
        
        <%-- Mostrar mensajes de error --%>
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <%-- Mostrar mensajes de éxito --%>
        <c:if test="${not empty success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        
        <form action="registro" method="post" id="registroForm">
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="nickname" class="form-label">Nickname *</label>
                    <input type="text" class="form-control" id="nickname" name="nickname" 
                           placeholder="Tu nombre de usuario" required minlength="3" maxlength="50">
                    <div class="form-text">Mínimo 3 caracteres. Este será tu nombre público.</div>
                </div>
                
                <div class="col-md-6 mb-3">
                    <label for="correo" class="form-label">Correo electrónico *</label>
                    <input type="email" class="form-control" id="correo" name="correo" 
                           placeholder="usuario@ejemplo.com" required>
                    <div class="form-text">No podrás cambiarlo después.</div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="password" class="form-label">Contraseña *</label>
                    <input type="password" class="form-control" id="password" name="password" 
                           placeholder="Mínimo 6 caracteres" required minlength="6">
                    <div class="password-strength" id="passwordStrength"></div>
                    <div class="form-text">Debe tener al menos 6 caracteres.</div>
                </div>
                
                <div class="col-md-6 mb-3">
                    <label for="confirmPassword" class="form-label">Confirmar contraseña *</label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                           placeholder="Repite tu contraseña" required>
                    <div id="passwordMatch" class="form-text"></div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="fechaNacimiento" class="form-label">Fecha de nacimiento *</label>
                    <input type="date" class="form-control" id="fechaNacimiento" name="fechaNacimiento" 
                           required max="<%= java.time.LocalDate.now().minusYears(13) %>">
                    <div class="form-text">Debes tener al menos 13 años.</div>
                </div>
                
                <div class="col-md-6 mb-3">
                    <label for="telefono" class="form-label">Teléfono *</label>
                    <input type="tel" class="form-control" id="telefono" name="telefono" 
                           placeholder="12345678" required pattern="[0-9]{8}" maxlength="8">
                    <div class="form-text">8 dígitos sin guiones.</div>
                </div>
            </div>
            
            <div class="mb-3">
                <label for="pais" class="form-label">País *</label>
                <select class="form-select" id="pais" name="pais" required>
                    <option value="">Selecciona tu país</option>
                    <option value="Guatemala">Guatemala</option>
                    <option value="Mexico">México</option>
                    <option value="El Salvador">El Salvador</option>
                    <option value="Honduras">Honduras</option>
                    <option value="Nicaragua">Nicaragua</option>
                    <option value="Costa Rica">Costa Rica</option>
                    <option value="Panama">Panamá</option>
                    <option value="Otro">Otro</option>
                </select>
            </div>
            
            <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" id="terminos" name="terminos" required>
                <label class="form-check-label" for="terminos">
                    Acepto los <a href="#" data-bs-toggle="modal" data-bs-target="#terminosModal">términos y condiciones</a> *
                </label>
            </div>
            
            <button type="submit" class="btn btn-success">Crear cuenta</button>
        </form>
        
        <div class="login-link">
            <p>¿Ya tienes una cuenta? <a href="login.jsp">Inicia sesión aquí</a></p>
        </div>
    </div>
    
    <!-- Modal Términos y Condiciones -->
    <div class="modal fade" id="terminosModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Términos y Condiciones</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <h6>1. Aceptación de términos</h6>
                    <p>Al registrarte en nuestra plataforma, aceptas cumplir con estos términos y condiciones.</p>
                    
                    <h6>2. Cuenta de usuario</h6>
                    <p>Eres responsable de mantener la confidencialidad de tu cuenta y contraseña.</p>
                    
                    <h6>3. Comportamiento</h6>
                    <p>Debes comportarte de manera respetuosa con otros usuarios.</p>
                    
                    <h6>4. Contenido</h6>
                    <p>Respetamos los derechos de autor y no permitimos contenido pirateado.</p>
                    
                    <h6>5. Modificaciones</h6>
                    <p>Nos reservamos el derecho de modificar estos términos en cualquier momento.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Validación de contraseñas coincidentes
        document.getElementById('confirmPassword').addEventListener('input', function() {
            const password = document.getElementById('password').value;
            const confirmPassword = this.value;
            const matchElement = document.getElementById('passwordMatch');
            
            if (confirmPassword === '') {
                matchElement.textContent = '';
                matchElement.style.color = '';
            } else if (password === confirmPassword) {
                matchElement.textContent = '✓ Las contraseñas coinciden';
                matchElement.style.color = 'green';
            } else {
                matchElement.textContent = '✗ Las contraseñas no coinciden';
                matchElement.style.color = 'red';
            }
        });
        
        // Validación de fortaleza de contraseña
        document.getElementById('password').addEventListener('input', function() {
            const password = this.value;
            const strengthBar = document.getElementById('passwordStrength');
            
            let strength = 0;
            if (password.length >= 6) strength++;
            if (password.length >= 8) strength++;
            if (/[A-Z]/.test(password)) strength++;
            if (/[0-9]/.test(password)) strength++;
            if (/[^A-Za-z0-9]/.test(password)) strength++;
            
            strengthBar.className = 'password-strength';
            if (strength <= 2) {
                strengthBar.classList.add('weak');
            } else if (strength <= 4) {
                strengthBar.classList.add('medium');
            } else {
                strengthBar.classList.add('strong');
            }
        });
        
        // Validación de edad mínima (13 años)
        const today = new Date();
        const maxDate = new Date(today.getFullYear() - 13, today.getMonth(), today.getDate());
        document.getElementById('fechaNacimiento').max = maxDate.toISOString().split('T')[0];
        
        // Validación del formulario
        document.getElementById('registroForm').addEventListener('submit', function(e) {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const terminos = document.getElementById('terminos').checked;
            
            if (password !== confirmPassword) {
                e.preventDefault();
                alert('Las contraseñas no coinciden. Por favor, verifica.');
                return false;
            }
            
            if (!terminos) {
                e.preventDefault();
                alert('Debes aceptar los términos y condiciones.');
                return false;
            }
            
            // Validar que tenga al menos 13 años
            const fechaNacimiento = new Date(document.getElementById('fechaNacimiento').value);
            const edad = today.getFullYear() - fechaNacimiento.getFullYear();
            const m = today.getMonth() - fechaNacimiento.getMonth();
            if (m < 0 || (m === 0 && today.getDate() < fechaNacimiento.getDate())) {
                edad--;
            }
            
            if (edad < 13) {
                e.preventDefault();
                alert('Debes tener al menos 13 años para registrarte.');
                return false;
            }
            
            return true;
        });
    </script>
</body>
</html>