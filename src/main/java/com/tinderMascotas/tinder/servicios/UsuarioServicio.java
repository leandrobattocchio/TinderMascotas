package com.tinderMascotas.tinder.servicios;

import com.tinderMascotas.tinder.entidades.Foto;
import com.tinderMascotas.tinder.entidades.Usuario;
import com.tinderMascotas.tinder.entidades.Zona;
import com.tinderMascotas.tinder.errores.ErrorServicio;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tinderMascotas.tinder.repositorios.UsuarioRepositorio;
import com.tinderMascotas.tinder.repositorios.ZonaRepositorio;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;


import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private ZonaRepositorio zonaRepositorio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private FotoServicio fotoServicio;

/////METODO PARA EL REGISTRO DEL USUARIO
    @Transactional
    public void registrar(MultipartFile archivo, String nombre, String apellido, String mail, String clave, String clave2, String idZona) throws ErrorServicio {

        Optional<Zona> zona = zonaRepositorio.findById(idZona);

        validar(nombre, apellido, mail, clave, clave2, zona, null);

        Zona zonaUsuario = zona.get();
        Usuario usuario = new Usuario();
        usuario.setZona(zonaUsuario);
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setMail(mail);

        String encriptada = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptada);

        usuario.setAlta(new Date());

        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);

        usuarioRepositorio.save(usuario);

        System.out.println(usuario);
        
        notificacionServicio.notificar(usuario);

    }
/////METODO PARA MODIFICAR LSO DATOS DEL USUARIO

    @Transactional
    public void modificar(MultipartFile archivo, String id, String nombre, String apellido, String mail, String clave, String clave2, String idZona) throws ErrorServicio {

        Optional<Zona> zona = zonaRepositorio.findById(idZona);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {

            Zona zonaUsuario = zona.get();
            Usuario usuario = respuesta.get();

            if (!usuario.getMail().equals(mail)) {
                validar(nombre, apellido, mail, clave, clave2, zona, null);
            } else {
                validar(nombre, apellido, mail, clave, clave2, zona, "validado");
            }

            usuario.setZona(zonaUsuario);
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setMail(mail);

            String encriptada = new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(encriptada);

            String idFoto = null;

            if (usuario.getFoto() != null) {
                idFoto = usuario.getFoto().getId();
            }

            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            usuario.setFoto(foto);

            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("El usuario buscado para modificar no se encontró");
        }

    }
/////METODO PARA DESHABILITAR

    @Transactional(propagation = Propagation.REQUIRED)
    public void deshabilitar(String id) throws ErrorServicio {

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setBaja(new Date());
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("El usuario buscado para deshabilitar no se encontró.");
        }

    }
/////METODO PARA HABILITAR USUARIO    

    @Transactional
    public void habilitar(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setBaja(null);
            usuarioRepositorio.save(usuario);
        } else {
            throw new ErrorServicio("El usuario buscado para habilitar no se encontró.");
        }

    }
/////METODO PARA VALIDAR LOS DATOS DEL USUARIO

    public void validar(String nombre, String apellido, String mail, String clave, String clave2, Optional zona, String validar) throws ErrorServicio {

        if (!zona.isPresent()) {
            throw new ErrorServicio("No ha seleccionado una zona valida para el usuario.");
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServicio("El nombre del usuario no puede estar vacio.");
        }

        if (apellido == null || apellido.trim().isEmpty()) {
            throw new ErrorServicio("El apellido del usuario no puede estar vacio.");
        }

        if (mail == null || mail.trim().isEmpty()) {
            throw new ErrorServicio("El mail del usuario no puede estar vacio.");
        }

        if (validar == null) {
            if (usuarioRepositorio.buscarUsuarioPorMail(mail) != null) {
                throw new ErrorServicio("Ya existe un usuario creado con ese email.");
            }
        }

        if (clave == null || clave.trim().isEmpty() || clave.length() <= 6) {
            throw new ErrorServicio("La clave del usuario no puede estar vacia, y debe tener mas de 6 digitos.");
        }

        if (!clave.equals(clave2)) {
            throw new ErrorServicio("Las contraseñas deben ser iguales.");
        }

    }

/////METODO PARA SEGURIDAD DE USUARIO    
    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarUsuarioPorMail(mail);

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList<>();

            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_USUARIO_REGISTRADO");
            permisos.add(p1);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);

            User user = new User(usuario.getMail(), usuario.getClave(), permisos);
            return user;
        } else {
            return null;
        }

    }

/////METODO PARA BUSCAR USUARIO POR ID    
    public Usuario buscarUsuarioPorId(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        Usuario usuario = new Usuario();

        if (respuesta.isPresent()) {
            usuario = respuesta.get();
        } else {
            throw new ErrorServicio("Error de servicio, usuario no encontrado.");
        }

        return usuario;
    }

    
    public List<Usuario> listarUsuarios(){
      return usuarioRepositorio.findAll();
    }
    
}
