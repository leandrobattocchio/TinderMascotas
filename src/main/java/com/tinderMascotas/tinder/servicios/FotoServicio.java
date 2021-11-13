package com.tinderMascotas.tinder.servicios;

import com.tinderMascotas.tinder.entidades.Foto;
import com.tinderMascotas.tinder.errores.ErrorServicio;
import com.tinderMascotas.tinder.repositorios.FotoRepositorio;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FotoServicio {

    @Autowired
    private FotoRepositorio fotoRepositorio;

/////METODO GUARDAR FOTO    
    @Transactional
    public Foto guardar(MultipartFile archivo) throws ErrorServicio {

        try {
            if (archivo != null && !archivo.isEmpty()) {

                Foto foto = new Foto();
                foto.setNombre(archivo.getName());
                foto.setMime(archivo.getContentType());
                foto.setContenido(archivo.getBytes());

                return fotoRepositorio.save(foto);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

/////METODO ACTUALIZAR FOTO    
    @Transactional
    public Foto actualizar(String idFoto, MultipartFile archivo) {

        try {
            if (archivo != null && !archivo.isEmpty()) {

                Foto foto = new Foto();

                if (idFoto != null) {

                    Optional<Foto> respuesta = fotoRepositorio.findById(idFoto);

                    if (respuesta.isPresent()) {
                        foto = respuesta.get();
                    }

                }

                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());
                foto.setContenido(archivo.getBytes());

                return fotoRepositorio.save(foto);

            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

}
