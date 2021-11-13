package com.tinderMascotas.tinder.controladores;

import com.tinderMascotas.tinder.entidades.Usuario;
import com.tinderMascotas.tinder.entidades.Zona;
import com.tinderMascotas.tinder.errores.ErrorServicio;
import com.tinderMascotas.tinder.repositorios.ZonaRepositorio;
import com.tinderMascotas.tinder.servicios.UsuarioServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class portalControlador {

    @Autowired
    private ZonaRepositorio zonaRepositorio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/")
    public String index() {
       
        return "index.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/inicio")
    public String inicio(){
        
       
        return "inicio.html";
    }
    
    @GetMapping("/registro")
    public String registro(ModelMap modelo) {
        List<Zona> zonas = zonaRepositorio.findAll();
        modelo.put("zonas", zonas);
        return "registro.html";
    }

    @GetMapping("/login")
    public String login(HttpSession session, ModelMap modelo, @RequestParam(required=false) String error, @RequestParam(required=false) String logout) {
        
        if (logout != null) {
            modelo.put("logout", "¡Se ha deslogueado correctamente de nuestro sitio web!");
        }
        
        
        if (error != null) {
            modelo.put("error", "¡El usuario o contraseña no coinciden!");
            session.invalidate();
        }
        
       
        return "login.html";
    }

    @PostMapping("/registrar")
    public String registrar(ModelMap modelo, @RequestParam MultipartFile archivo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String mail, @RequestParam String clave1, @RequestParam String clave2, @RequestParam String idZona) {

        try {
            usuarioServicio.registrar(archivo, nombre, apellido, mail, clave1, clave2, idZona);
        } catch (ErrorServicio e) {
            List<Zona> zonas = zonaRepositorio.findAll();
            modelo.put("zonas", zonas);
            modelo.put("error", e.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("mail", mail);
            modelo.put("clave1", clave1);
            modelo.put("clave2", clave2);
            return "registro.html";
        }

        modelo.put("titulo", "Registro exitoso");
        modelo.put("descripcion", "Usted se ha logrado registrar exitosamente en nuestro sitio web.");
        return "exito.html";
    }

}
