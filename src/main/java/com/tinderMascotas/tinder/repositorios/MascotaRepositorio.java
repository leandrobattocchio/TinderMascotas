package com.tinderMascotas.tinder.repositorios;

import com.tinderMascotas.tinder.entidades.Mascota;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MascotaRepositorio extends JpaRepository<Mascota, String> {

    @Query("SELECT c FROM Mascota c WHERE c.usuario.id = :id AND c.baja IS NULL ORDER BY c.nombre")
    public List<Mascota> buscarMascotasPorUsuario(@Param("id") String id);
}
