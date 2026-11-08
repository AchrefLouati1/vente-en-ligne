package com.khalil.projet.repository;

import com.khalil.projet.dto.ProduitVenteDTO;
import com.khalil.projet.model.Produit;
import com.khalil.projet.model.StatutCommande;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface ProduitRepository extends JpaRepository<Produit, Long> {

    // --- Requêtes dérivées (Spring génère le SQL à partir du nom de la méthode) ---

    List<Produit> findByCategorieId(Long categorieId);

    List<Produit> findByNomContainingIgnoreCase(String nom);

    List<Produit> findByPrixBetweenOrderByPrixAsc(BigDecimal min, BigDecimal max);

    /** Besoin métier : alerte de réapprovisionnement. */
    List<Produit> findByStockLessThanEqualOrderByStockAsc(Integer seuil);

    boolean existsByCategorieId(Long categorieId);

    // --- Requête personnalisée JPQL ---

    /** Besoin métier : classement des produits les plus vendus (hors commandes annulées). */
    @Query("""
            SELECT new com.khalil.projet.dto.ProduitVenteDTO(
                       p.id, p.nom, SUM(l.quantite), SUM(l.prixUnitaire * l.quantite))
            FROM LigneCommande l JOIN l.produit p
            WHERE l.commande.statut IN :statuts
            GROUP BY p.id, p.nom
            ORDER BY SUM(l.quantite) DESC
            """)
    List<ProduitVenteDTO> findMeilleuresVentes(@Param("statuts") Collection<StatutCommande> statuts,
                                               Pageable pageable);
}
