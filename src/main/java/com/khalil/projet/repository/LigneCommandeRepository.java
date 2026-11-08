package com.khalil.projet.repository;

import com.khalil.projet.model.LigneCommande;
import com.khalil.projet.model.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    boolean existsByProduitId(Long produitId);

    /** Le client a-t-il reçu ce produit ? (condition pour laisser un avis) */
    boolean existsByCommandeClientIdAndProduitIdAndCommandeStatut(Long clientId, Long produitId,
                                                                  StatutCommande statut);
}
