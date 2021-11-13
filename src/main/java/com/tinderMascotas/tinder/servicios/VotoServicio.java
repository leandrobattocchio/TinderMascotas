package com.tinderMascotas.tinder.servicios;

import com.tinderMascotas.tinder.entidades.Mascota;
import com.tinderMascotas.tinder.entidades.Voto;
import com.tinderMascotas.tinder.errores.ErrorServicio;
import java.util.Date;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tinderMascotas.tinder.repositorios.VotoRepositorio;
import com.tinderMascotas.tinder.repositorios.MascotaRepositorio;
import javax.transaction.Transactional;

@Service
public class VotoServicio {

  

    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    @Autowired
    private VotoRepositorio votoRepositorio;

/////METODO PARA VOTAR
    @Transactional
    public void votar(String idUsuario, String idMascota1, String idMascota2) throws ErrorServicio {

        Voto voto = new Voto();
        voto.setFecha(new Date());

        if (idMascota1.equals(idMascota2)) {
            throw new ErrorServicio("No puede autovotarse.");
        }

        Optional<Mascota> respuesta1 = mascotaRepositorio.findById(idMascota1);

        if (respuesta1.isPresent()) {

            Mascota mascota1 = respuesta1.get();

            if (mascota1.getUsuario().getId().equals(idUsuario)) {

                voto.setMascota1(mascota1);

            } else {
                throw new ErrorServicio("No tiene los permisos suficientes para realizar esta acción.");
            }

        } else {
            throw new ErrorServicio("La mascota emisora del voto no se encontró.");
        }

        Optional<Mascota> respuesta2 = mascotaRepositorio.findById(idMascota2);

        if (respuesta2.isPresent()) {

            Mascota mascota2 = respuesta2.get();
            voto.setMascota2(mascota2);
            

        } else {
            throw new ErrorServicio("La mascota a votar no se encontró.");
        }

        votoRepositorio.save(voto);

    }

/////METODO RESPUESTA VOTO    
    @Transactional
    public void responder(String idUsuario, String idVoto) throws ErrorServicio {

        Optional<Voto> respuesta = votoRepositorio.findById(idVoto);

        if (respuesta.isPresent()) {

            Voto voto = respuesta.get(); 

            if (voto.getMascota2().getUsuario().getId().equals(idUsuario)) {

                voto.setRespuesta(new Date());
                
                votoRepositorio.save(voto);

            } else {
                throw new ErrorServicio("No tiene permisos para realizar esta acción.");
            }

        } else {
            throw new ErrorServicio("No se encontró el voto a buscar.");
        }

    }
}
