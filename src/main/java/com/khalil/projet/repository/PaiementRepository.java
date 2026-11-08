package com.khalil.projet.repository;

import com.khalil.projet.model.Paiement;
import com.khalil.projet.model.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    Optional<Paiement> findByCommandeId(Long commandeId);

    List<Paiement> findByStatut(StatutPaiement statut);
}
