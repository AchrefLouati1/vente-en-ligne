package com.khalil.projet.service;

import com.khalil.projet.dto.AvisRequest;
import com.khalil.projet.dto.NoteMoyenneDTO;
import com.khalil.projet.exception.BusinessException;
import com.khalil.projet.exception.ConflictException;
import com.khalil.projet.exception.ResourceNotFoundException;
import com.khalil.projet.model.Avis;
import com.khalil.projet.model.Client;
import com.khalil.projet.model.Produit;
import com.khalil.projet.model.StatutCommande;
import com.khalil.projet.repository.AvisRepository;
import com.khalil.projet.repository.LigneCommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AvisService {

    private final AvisRepository avisRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final ClientService clientService;
    private final ProduitService produitService;

    public List<Avis> findAll() {
        return avisRepository.findAll();
    }

    public Avis findById(Long id) {
        return avisRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avis", id));
    }

    public List<Avis> findByProduit(Long produitId) {
        produitService.findById(produitId);
        return avisRepository.findByProduitIdOrderByDateAvisDesc(produitId);
    }

    public Avis create(AvisRequest request) {
        Client client = clientService.findById(request.clientId());
        Produit produit = produitService.findById(request.produitId());

        if (avisRepository.existsByClientIdAndProduitId(client.getId(), produit.getId())) {
            throw new ConflictException("Ce client a déjà donné un avis sur ce produit");
        }
        // Règle métier : seul un client qui a reçu le produit peut le noter
        boolean aRecuLeProduit = ligneCommandeRepository.existsByCommandeClientIdAndProduitIdAndCommandeStatut(
                client.getId(), produit.getId(), StatutCommande.LIVREE);
        if (!aRecuLeProduit) {
            throw new BusinessException("Le client doit avoir reçu ce produit (commande LIVREE) pour laisser un avis");
        }

        Avis avis = new Avis();
        avis.setClient(client);
        avis.setProduit(produit);
        avis.setNote(request.note());
        avis.setCommentaire(request.commentaire());
        return avisRepository.save(avis);
    }

    /** Seuls la note et le commentaire sont modifiables. */
    public Avis update(Long id, AvisRequest request) {
        Avis avis = findById(id);
        avis.setNote(request.note());
        avis.setCommentaire(request.commentaire());
        return avis;
    }

    public void delete(Long id) {
        avisRepository.delete(findById(id));
    }

    public NoteMoyenneDTO noteMoyenne(Long produitId) {
        Produit produit = produitService.findById(produitId);
        Double moyenne = avisRepository.calculerNoteMoyenne(produitId);
        long nombre = avisRepository.countByProduitId(produitId);
        Double arrondie = moyenne == null ? null : Math.round(moyenne * 10) / 10.0;
        return new NoteMoyenneDTO(produit.getId(), produit.getNom(), arrondie, nombre);
    }
}
