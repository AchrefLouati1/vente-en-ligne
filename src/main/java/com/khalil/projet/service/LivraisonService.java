package com.khalil.projet.service;

import com.khalil.projet.dto.LivraisonRequest;
import com.khalil.projet.exception.BusinessException;
import com.khalil.projet.exception.ResourceNotFoundException;
import com.khalil.projet.model.Livraison;
import com.khalil.projet.model.StatutLivraison;
import com.khalil.projet.repository.LivraisonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Consultation / modification des livraisons. La création passe par CommandeService.expedier(). */
@Service
@RequiredArgsConstructor
@Transactional
public class LivraisonService {

    private final LivraisonRepository livraisonRepository;

    public List<Livraison> findAll() {
        return livraisonRepository.findAll();
    }

    public List<Livraison> findByStatut(StatutLivraison statut) {
        return livraisonRepository.findByStatut(statut);
    }

    public Livraison findById(Long id) {
        return livraisonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livraison", id));
    }

    /** On peut corriger l'adresse ou le transporteur tant que le colis n'est pas livré. */
    public Livraison update(Long id, LivraisonRequest request) {
        Livraison livraison = findById(id);
        if (livraison.getStatut() == StatutLivraison.LIVREE) {
            throw new BusinessException("Impossible de modifier une livraison déjà effectuée");
        }
        livraison.setAdresse(request.adresse());
        livraison.setVille(request.ville());
        livraison.setCodePostal(request.codePostal());
        livraison.setTransporteur(request.transporteur());
        return livraison;
    }
}
