
package com.tinderMascotas.tinder.controladores;

import com.tinderMascotas.tinder.entidades.Mascota;
import com.tinderMascotas.tinder.entidades.Usuario;
import com.tinderMascotas.tinder.errores.ErrorServicio;
import com.tinderMascotas.tinder.servicios.MascotaServicio;
import com.tinderMascotas.tinder.servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;




@Controller
@RequestMapping("/foto")
public class FotoControlador {
    
    
    @Autowired
    private UsuarioServicio usuarioServicio;
    
    @Autowired
    private MascotaServicio mascotaServicio;
    
    
    @GetMapping("/usuario/{id}")
    public ResponseEntity<byte[]>fotoUsuario(@PathVariable String id) {
             
        try {
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(id);
            
            if (usuario.getFoto() == null) {
                throw new ErrorServicio("No se encontro una foto para este usuario.");
            }
   
            byte[] foto = usuario.getFoto().getContenido();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            
            
            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
            
        } catch (ErrorServicio ex) {
           
            return new ResponseEntity(HttpStatus.NOT_FOUND);
            
        }
        
        
        
        
    }
    
    
    @GetMapping("/mascota/{id}")
    public ResponseEntity<byte[]>fotoMascota(@PathVariable String id) {
             
        try {
            
            Mascota mascota = mascotaServicio.buscarMascotaPorId(id);
            
            if (mascota.getFoto() == null) {
                throw new ErrorServicio("No se encontro una foto para esta mascota.");
            }
   
            byte[] foto = mascota.getFoto().getContenido();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            
            
            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
            
        } catch (ErrorServicio ex) {
           
            return new ResponseEntity(HttpStatus.NOT_FOUND);
            
        }
        
        
        
        
    }
}
