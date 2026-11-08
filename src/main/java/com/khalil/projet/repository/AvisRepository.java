package com.khalil.projet.repository;

import com.khalil.projet.model.Avis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AvisRepository extends JpaRepository<Avis, Long> {

    List<Avis> findByProduitIdOrderByDateAvisDesc(Long produitId);

    boolean existsByClientIdAndProduitId(Long clientId, Long produitId);

    long countByProduitId(Long produitId);

    /** Besoin métier : note moyenne affichée sur la fiche produit. */
    @Query("SELECT AVG(a.note) FROM Avis a WHERE a.produit.id = :produitId")
    Double calculerNoteMoyenne(@Param("produitId") Long produitId);
}
