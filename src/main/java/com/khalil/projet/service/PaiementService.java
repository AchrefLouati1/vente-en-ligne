package com.khalil.projet.service;

import com.khalil.projet.exception.ResourceNotFoundException;
import com.khalil.projet.model.Paiement;
import com.khalil.projet.model.StatutPaiement;
import com.khalil.projet.repository.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Consultation des paiements. La création passe par CommandeService.payer(). */
@Service
@RequiredArgsConstructor
@Transactional
public class PaiementService {

    private final PaiementRepository paiementRepository;

    public List<Paiement> findAll() {
        return paiementRepository.findAll();
    }

    public List<Paiement> findByStatut(StatutPaiement statut) {
        return paiementRepository.findByStatut(statut);
    }

    public Paiement findById(Long id) {
        return paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", id));
    }
}
