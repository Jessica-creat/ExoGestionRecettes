package com.example.gestionrecettes.repository;

import com.example.gestionrecettes.model.Recette;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecetteRepository extends JpaRepository<Recette, Long> {
    List<Recette> findByCategorieId(Long categorieId);
}
