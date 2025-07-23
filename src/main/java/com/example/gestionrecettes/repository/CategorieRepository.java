package com.example.gestionrecettes.repository;

import com.example.gestionrecettes.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategorieRepository extends JpaRepository<Categorie, Long> {
    @Query("SELECT COUNT(r) > 0 FROM Recette r WHERE r.categorie.id = :categorieId")
    boolean isUsedInRecettes(@Param("categorieId") Long categorieId);
}
