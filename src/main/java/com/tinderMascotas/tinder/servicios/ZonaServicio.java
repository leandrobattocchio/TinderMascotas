package com.tinderMascotas.tinder.servicios;

import com.tinderMascotas.tinder.entidades.Zona;
import com.tinderMascotas.tinder.repositorios.ZonaRepositorio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZonaServicio {

    @Autowired
    private ZonaRepositorio zonaRepositorio;

    public List<Zona> listaZonas() {
        List<Zona> zonas = zonaRepositorio.findAll();
        return zonas;
    }
}
