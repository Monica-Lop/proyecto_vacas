package com.vacas.service;

import com.vacas.dao.GrupoFamiliarDAO;
import com.vacas.model.GrupoFamiliar;
import com.vacas.model.MiembroGrupo;
import java.util.List;

public class GrupoFamiliarService {
    private GrupoFamiliarDAO grupoFamiliarDAO;
    
    public GrupoFamiliarService() {
        this.grupoFamiliarDAO = new GrupoFamiliarDAO();
    }
    
    public int crearGrupo(GrupoFamiliar grupo) {
        int grupoId = grupoFamiliarDAO.crear(grupo);
        
        if (grupoId > 0) {
            // Agregar al administrador como miembro activo
            MiembroGrupo miembro = new MiembroGrupo();
            miembro.setGrupoId(grupoId);
            miembro.setUsuarioId(grupo.getAdminId());
            miembro.setEstado("ACTIVO");
            grupoFamiliarDAO.agregarMiembro(miembro);
        }
        
        return grupoId;
    }
    
    public boolean invitarMiembro(int grupoId, int usuarioId) {
        MiembroGrupo miembro = new MiembroGrupo();
        miembro.setGrupoId(grupoId);
        miembro.setUsuarioId(usuarioId);
        miembro.setEstado("PENDIENTE");
        
        return grupoFamiliarDAO.agregarMiembro(miembro);
    }
    
    public boolean aceptarInvitacion(int grupoId, int usuarioId) {
        return grupoFamiliarDAO.actualizarEstadoMiembro(grupoId, usuarioId, "ACTIVO");
    }
    
    public boolean rechazarInvitacion(int grupoId, int usuarioId) {
        return grupoFamiliarDAO.actualizarEstadoMiembro(grupoId, usuarioId, "RECHAZADO");
    }
    
    public List<MiembroGrupo> obtenerMiembros(int grupoId) {
        return grupoFamiliarDAO.obtenerMiembros(grupoId);
    }
    
    public List<GrupoFamiliar> obtenerGruposPorUsuario(int usuarioId) {
        return grupoFamiliarDAO.obtenerGruposPorUsuario(usuarioId);
    }
}