package com.tinderMascotas.tinder.controladores;

import com.tinderMascotas.tinder.entidades.Usuario;
import com.tinderMascotas.tinder.entidades.Zona;
import com.tinderMascotas.tinder.errores.ErrorServicio;
import com.tinderMascotas.tinder.servicios.UsuarioServicio;
import com.tinderMascotas.tinder.servicios.ZonaServicio;
import java.util.List;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    private ZonaServicio zonaServicio;

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, ModelMap modelo, @RequestParam String id) {

        Usuario login = (Usuario) session.getAttribute("usuariosession");

        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";

        }

        try {
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(id);
            modelo.addAttribute("perfil", usuario);
        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e);
        }

        List<Zona> zonas = zonaServicio.listaZonas();
        modelo.addAttribute("zonas ", zonas);

        return "perfil.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/actualizar-perfil")
    public String modificarPerfil(ModelMap modelo, HttpSession session, MultipartFile archivo, @RequestParam String id, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String mail, @RequestParam String clave, @RequestParam String clave2, @RequestParam String idZona) {

        Usuario usuario = null;

        Usuario login = (Usuario) session.getAttribute("usuariosession");

        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }

        try {

            usuario = usuarioServicio.buscarUsuarioPorId(id);
            usuarioServicio.modificar(archivo, id, nombre, apellido, mail, clave, clave2, idZona);
            session.setAttribute("usuariosession", usuario);

            return "redirect:/inicio";
        } catch (ErrorServicio e) {

            List<Zona> zonas = zonaServicio.listaZonas();
            modelo.addAttribute("zonas", zonas);
            modelo.addAttribute("error", e.getMessage());
            modelo.addAttribute("perfil", usuario);

            return "perfil.html";
        }

    }

    @GetMapping("/baja")
    public String darBajaUusario(HttpSession session, RedirectAttributes modelo, @RequestParam(required = true) String id) {

        try {
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(id);

            if (usuario != null) {
                usuarioServicio.deshabilitar(usuario.getId());
                modelo.addFlashAttribute("deshabilitado", "Su usuario ha sido deshabilitado correctamente!");
                
                return "redirect:/logout";
            } else {
                modelo.addFlashAttribute("deshabilitado", "Su usuario no ha sido deshabilitado correctamente!");
                return "redirect:/inicio";
            }

        } catch (ErrorServicio e) {
            modelo.addFlashAttribute("deshabilitado", "Ha ocurrido un fallo del sistema al intentar deshabilitar su usuario!");
            return "redirect:/inicio";
        }

    }

    @RequestMapping("listar-usuarios")
    public List<Usuario> listarUsuarios(){
        
       return usuarioServicio.listarUsuarios();
        
    }
    
}
