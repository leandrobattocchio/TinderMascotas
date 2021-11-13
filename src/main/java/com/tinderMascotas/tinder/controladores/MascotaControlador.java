package com.tinderMascotas.tinder.controladores;

import com.tinderMascotas.tinder.entidades.Mascota;
import com.tinderMascotas.tinder.entidades.Usuario;
import com.tinderMascotas.tinder.enumeraciones.SexoAnimal;
import com.tinderMascotas.tinder.enumeraciones.Tipo;
import com.tinderMascotas.tinder.errores.ErrorServicio;
import com.tinderMascotas.tinder.servicios.MascotaServicio;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/mascota")
public class MascotaControlador {

    @Autowired
    private MascotaServicio mascotaServicio;

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/editar-perfil")
    public String perfilMascota(ModelMap modelo, @RequestParam(required = false) String id, @RequestParam(required=false) String accion, @RequestParam(required=false) String proceso) {

        Mascota mascota = new Mascota();

        
        if (id != null && !id.isEmpty()) {

            try {
                mascota = mascotaServicio.buscarMascotaPorId(id);

            } catch (ErrorServicio ex) {
                Logger.getLogger(MascotaControlador.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        modelo.put("perfil", mascota);
        modelo.put("sexos", SexoAnimal.values());
        modelo.put("tipos", Tipo.values());
        modelo.put("accion", accion);
        modelo.put("proceso", proceso);
        
        return "mascota.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/actualizar-perfil")
    public String actualizarMascota(HttpSession session, ModelMap modelo, MultipartFile archivo, @RequestParam String idMascota, @RequestParam String nombre, @RequestParam Tipo tipo, @RequestParam SexoAnimal sexo) {

        Usuario usuario = (Usuario) session.getAttribute("usuariosession");

        try {

            if (idMascota == null || idMascota.isEmpty()) {
                mascotaServicio.agregarMascota(archivo, usuario.getId(), nombre, sexo, tipo);
            } else {

                mascotaServicio.modificarMascota(archivo, usuario.getId(), idMascota, nombre, sexo, tipo);
            }

            return "redirect:/inicio";

        } catch (ErrorServicio e) {

            Mascota mascota = new Mascota();

            mascota.setId(idMascota);
            mascota.setNombre(nombre);
            mascota.setSexo(sexo);
            mascota.setTipo(tipo);

            modelo.addAttribute("error", e.getMessage());
            modelo.addAttribute("perfil", mascota);
            modelo.put("sexos", SexoAnimal.values());
            modelo.put("tipos", Tipo.values());

            return "mascota.html";
        }

    }

    @GetMapping("/mis-mascotas")
    public String listaMascotas(ModelMap modelo, HttpSession session){
        
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        
        if (usuario == null) {
             
            return "redirect:/inicio";
        }
        
        List<Mascota> mascotas = mascotaServicio.listaMascotas(usuario.getId());
        
        modelo.put("mascotas", mascotas);
        return "lista-mascotas.html";
    }
    
    
    
    
    
    @PostMapping("/eliminar-perfil")
    public String eliminarMascota(ModelMap modelo, HttpSession session, @RequestParam(required=true)String idMascota){
        
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        
        try {
     
            if (usuario == null) {
                return "redirect:/inicio";
            }
            
            mascotaServicio.eliminarMascota(usuario.getId(), idMascota);
        } catch (ErrorServicio ex) {
            Logger.getLogger(MascotaControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        List<Mascota> mascotas = mascotaServicio.listaMascotas(usuario.getId());
        modelo.put("mascotas", mascotas);
        
        return "lista-mascotas.html";
    }
}
