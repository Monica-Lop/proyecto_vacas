package com.vacas.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/api/*")
public class AuthFilter implements Filter {
    
    private static final String[] PUBLIC_PATHS = {
        "/api/login",
        "/api/registro",
        "/api/auth"
    };
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI().substring(request.getContextPath().length());
        
        // Verificar si la ruta es pública
        boolean isPublicPath = false;
        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                isPublicPath = true;
                break;
            }
        }
        
        if (!isPublicPath) {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("usuario") == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"No autenticado\"}");
                return;
            }
        }
        
        chain.doFilter(req, res);
    }
    
    @Override public void init(FilterConfig filterConfig) {}
    @Override public void destroy() {}
}