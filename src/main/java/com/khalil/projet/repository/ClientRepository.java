package com.khalil.projet.repository;

import com.khalil.projet.dto.ClientDepenseDTO;
import com.khalil.projet.model.Client;
import com.khalil.projet.model.StatutCommande;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    /** Clients ayant ce produit dans leurs favoris (relation ManyToMany). */
    List<Client> findByFavorisId(Long produitId);

    /** Besoin métier : fidélisation, clients ayant le plus dépensé. */
    @Query("""
            SELECT new com.khalil.projet.dto.ClientDepenseDTO(
                       c.id, c.nom, c.email, COUNT(co), SUM(co.montantTotal))
            FROM Commande co JOIN co.client c
            WHERE co.statut IN :statuts
            GROUP BY c.id, c.nom, c.email
            ORDER BY SUM(co.montantTotal) DESC
            """)
    List<ClientDepenseDTO> findMeilleursClients(@Param("statuts") Collection<StatutCommande> statuts,
                                                Pageable pageable);
}
