package com.tinderMascotas.tinder.servicios;

import com.tinderMascotas.tinder.entidades.Foto;
import com.tinderMascotas.tinder.entidades.Mascota;
import com.tinderMascotas.tinder.entidades.Usuario;
import com.tinderMascotas.tinder.enumeraciones.SexoAnimal;
import com.tinderMascotas.tinder.enumeraciones.Tipo;
import com.tinderMascotas.tinder.errores.ErrorServicio;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tinderMascotas.tinder.repositorios.UsuarioRepositorio;
import com.tinderMascotas.tinder.repositorios.MascotaRepositorio;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MascotaServicio {
    
    @Autowired
    private MascotaRepositorio mascotaRepositorio;
    
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    @Autowired
    private FotoServicio fotoServicio;

/////METODO PARA AGREGAR MASCOTA    
    @Transactional
    public void agregarMascota(MultipartFile archivo, String idUsuario, String nombre, SexoAnimal sexo, Tipo tipo) throws ErrorServicio {
        
        Optional<Usuario> respuesta = usuarioRepositorio.findById(idUsuario);
        
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            
            validar(nombre, sexo, tipo);
            
            Mascota mascota = new Mascota();
            mascota.setNombre(nombre);
            mascota.setSexo(sexo);
            mascota.setAlta(new Date());
            mascota.setUsuario(usuario);
            mascota.setTipo(tipo);
            
            Foto foto = fotoServicio.guardar(archivo);
            mascota.setFoto(foto);
            
            mascotaRepositorio.save(mascota);
        } else {
            throw new ErrorServicio("La id de usuario ingresada no coincide con ninguno de nuestros usuarios.");
        }
        
    }
/////METODO PARA MODIFICAR MASCOTA    

    @Transactional
    public void modificarMascota(MultipartFile archivo, String idUsuario, String idMascota, String nombre, SexoAnimal sexo, Tipo tipo) throws ErrorServicio {
        
        validar(nombre, sexo, tipo);
        
        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota);
        
        if (respuesta.isPresent()) {
            
            Mascota mascota = respuesta.get();
            
            if (mascota.getUsuario().getId().equals(idUsuario)) {
                
                mascota.setNombre(nombre);
                mascota.setSexo(sexo);
                mascota.setTipo(tipo);
                
                String idFoto = null;
                
                if (mascota.getFoto() != null) {
                    idFoto = mascota.getFoto().getId();
                }
                
                Foto foto = fotoServicio.actualizar(idFoto, archivo);
                mascota.setFoto(foto);
                
                mascotaRepositorio.save(mascota);
            } else {
                throw new ErrorServicio("Usted no tiene suficientes permisos de usuario para modificar esta mascota.");
            }
            
        } else {
            throw new ErrorServicio("La mascota no se encontró.");
        }
    }
/////METODO PARA ELIMINAR MASCOTA    
    @Transactional
    public void eliminarMascota(String idUsuario, String idMascota) throws ErrorServicio {
        
        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota);
        
        if (respuesta.isPresent()) {
            
            Mascota mascota = respuesta.get();
            
            if (mascota.getUsuario().getId().equals(idUsuario)) {
                mascota.setBaja(new Date());
                mascotaRepositorio.save(mascota);
            } else {
                throw new ErrorServicio("Usted no tiene suficientes permisos de usuario para eliminar esta mascota.");
            }
            
        } else {
            throw new ErrorServicio("La mascota no se encontro");
        }
    }
    /////METODO PARA HABILITAR MASCOTA
    @Transactional
    public void habilitarMascota(String idUsuario, String idMascota) throws ErrorServicio {
        
        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota);
        
        if (respuesta.isPresent()) {
            
            Mascota mascota = respuesta.get();
            
            if (mascota.getUsuario().getId().equals(idUsuario)) {
                mascota.setBaja(null);
                mascotaRepositorio.save(mascota);
            } else {
                throw new ErrorServicio("Usted no tiene suficientes permisos de usuario para habilitar esta mascota.");
            }
            
        } else {
            throw new ErrorServicio("La mascota no se encontro");
        }
    }
    
    
    /////METODO PARA VALIDAR MASCOTA  

    public void validar(String nombre, SexoAnimal sexo, Tipo tipo) throws ErrorServicio {
        
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServicio("El nombre de la mascota no puede ser nulo o estar vacio.");
        }
        
        if (sexo == null) {
            throw new ErrorServicio("El sexo de la mascota no puede ser nulo.");
        }
        
         if (tipo == null) {
            throw new ErrorServicio("El sexo de la mascota no puede ser nulo.");
        }
    }
    
/////BUSCAR MASCOTA POR ID    
    public Mascota buscarMascotaPorId(String id) throws ErrorServicio {
        
        Optional<Mascota> respuesta = mascotaRepositorio.findById(id);
        
        if (respuesta.isPresent()) {
         Mascota  mascota = respuesta.get();
            return mascota;
        } else {
            throw new ErrorServicio("Error de servicio, mascota no encontrada.");
        }

    }
    
    
/////LISTAR MASCOTAS    
    public List<Mascota> listaMascotas(String id){
        
        List<Mascota> mascotas = mascotaRepositorio.buscarMascotasPorUsuario(id);
        return mascotas;   
    }
    
}
