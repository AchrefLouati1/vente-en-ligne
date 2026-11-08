package com.khalil.projet.repository;

import com.khalil.projet.model.Commande;
import com.khalil.projet.model.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    /** Historique d'un client, de la plus récente à la plus ancienne. */
    List<Commande> findByClientIdOrderByDateCommandeDesc(Long clientId);

    List<Commande> findByStatut(StatutCommande statut);

    boolean existsByClientId(Long clientId);

    long countByStatutInAndDateCommandeBetween(Collection<StatutCommande> statuts,
                                               LocalDateTime debut, LocalDateTime fin);

    /** Besoin métier : chiffre d'affaires encaissé sur une période. */
    @Query("""
            SELECT SUM(c.montantTotal) FROM Commande c
            WHERE c.statut IN :statuts
              AND c.dateCommande BETWEEN :debut AND :fin
            """)
    BigDecimal calculerChiffreAffaires(@Param("statuts") Collection<StatutCommande> statuts,
                                       @Param("debut") LocalDateTime debut,
                                       @Param("fin") LocalDateTime fin);
}
