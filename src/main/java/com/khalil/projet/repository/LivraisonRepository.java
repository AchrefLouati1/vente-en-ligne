package com.khalil.projet.repository;

import com.khalil.projet.model.Livraison;
import com.khalil.projet.model.StatutLivraison;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivraisonRepository extends JpaRepository<Livraison, Long> {

    Optional<Livraison> findByCommandeId(Long commandeId);

    List<Livraison> findByStatut(StatutLivraison statut);
}
