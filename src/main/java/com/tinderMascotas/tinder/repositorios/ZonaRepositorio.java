
package com.tinderMascotas.tinder.repositorios;

import com.tinderMascotas.tinder.entidades.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZonaRepositorio extends JpaRepository<Zona, String> {
     
}
