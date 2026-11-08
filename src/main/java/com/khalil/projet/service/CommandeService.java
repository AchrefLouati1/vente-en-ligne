package com.khalil.projet.service;

import com.khalil.projet.dto.CommandeRequest;
import com.khalil.projet.dto.LigneCommandeRequest;
import com.khalil.projet.dto.LivraisonRequest;
import com.khalil.projet.dto.PaiementRequest;
import com.khalil.projet.exception.BusinessException;
import com.khalil.projet.exception.ResourceNotFoundException;
import com.khalil.projet.model.*;
import com.khalil.projet.repository.CommandeRepository;
import com.khalil.projet.repository.LivraisonRepository;
import com.khalil.projet.repository.PaiementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Cycle de vie d'une commande :
 * EN_ATTENTE -> PAYEE -> EXPEDIEE -> LIVREE
 *      \-> ANNULEE (possible tant que la commande n'est pas expédiée)
 *
 * Toutes les méthodes sont transactionnelles : si une erreur survient
 * (ex : stock insuffisant sur la 2e ligne), tout est annulé (rollback).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CommandeService {

    private final CommandeRepository commandeRepository;
    private final PaiementRepository paiementRepository;
    private final LivraisonRepository livraisonRepository;
    private final ClientService clientService;
    private final ProduitService produitService;

    public List<Commande> findAll() {
        return commandeRepository.findAll();
    }

    public Commande findById(Long id) {
        return commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", id));
    }

    public List<Commande> findByStatut(StatutCommande statut) {
        return commandeRepository.findByStatut(statut);
    }

    /** Historique des commandes d'un client. */
    public List<Commande> findByClient(Long clientId) {
        clientService.findById(clientId);
        return commandeRepository.findByClientIdOrderByDateCommandeDesc(clientId);
    }

    public Commande creer(CommandeRequest request) {
        Commande commande = new Commande();
        commande.setClient(clientService.findById(request.clientId()));
        commande.setStatut(StatutCommande.EN_ATTENTE);
        remplirLignes(commande, request.lignes());
        return commandeRepository.save(commande);
    }

    /** Modifier les lignes d'une commande : seulement tant qu'elle n'est pas payée. */
    public Commande modifier(Long id, CommandeRequest request) {
        Commande commande = findById(id);
        verifierStatut(commande, StatutCommande.EN_ATTENTE, "modifiée");
        if (!commande.getClient().getId().equals(request.clientId())) {
            commande.setClient(clientService.findById(request.clientId()));
        }
        restaurerStock(commande);
        commande.getLignes().clear();
        remplirLignes(commande, request.lignes());
        return commandeRepository.save(commande);
    }

    public Commande annuler(Long id) {
        Commande commande = findById(id);
        StatutCommande statut = commande.getStatut();
        if (statut != StatutCommande.EN_ATTENTE && statut != StatutCommande.PAYEE) {
            throw new BusinessException("Impossible d'annuler une commande au statut " + statut);
        }
        restaurerStock(commande);
        if (statut == StatutCommande.PAYEE) {
            paiementRepository.findByCommandeId(id)
                    .ifPresent(p -> p.setStatut(StatutPaiement.REMBOURSE));
        }
        commande.setStatut(StatutCommande.ANNULEE);
        return commande;
    }

    /** Suppression : seulement une commande en attente ou annulée. */
    public void supprimer(Long id) {
        Commande commande = findById(id);
        StatutCommande statut = commande.getStatut();
        if (statut != StatutCommande.EN_ATTENTE && statut != StatutCommande.ANNULEE) {
            throw new BusinessException("Seule une commande EN_ATTENTE ou ANNULEE peut être supprimée (statut actuel : "
                    + statut + ")");
        }
        if (statut == StatutCommande.EN_ATTENTE) {
            restaurerStock(commande);
        }
        paiementRepository.findByCommandeId(id).ifPresent(paiementRepository::delete);
        commandeRepository.delete(commande);
    }

    // ----- Paiement -----

    public Paiement payer(Long id, PaiementRequest request) {
        Commande commande = findById(id);
        verifierStatut(commande, StatutCommande.EN_ATTENTE, "payée");

        // Règle métier : le montant payé doit correspondre exactement au total
        if (request.montant().compareTo(commande.getMontantTotal()) != 0) {
            throw new BusinessException("Paiement refusé : le montant " + request.montant()
                    + " ne correspond pas au total de la commande (" + commande.getMontantTotal()
                    + "). La commande reste EN_ATTENTE.");
        }

        Paiement paiement = new Paiement();
        paiement.setCommande(commande);
        paiement.setMontant(request.montant());
        paiement.setMethode(request.methode());
        paiement.setStatut(StatutPaiement.PAYE);
        paiement.setDatePaiement(LocalDateTime.now());

        commande.setStatut(StatutCommande.PAYEE);
        return paiementRepository.save(paiement);
    }

    public Paiement getPaiement(Long commandeId) {
        findById(commandeId);
        return paiementRepository.findByCommandeId(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun paiement pour la commande " + commandeId));
    }

    // ----- Livraison -----

    public Livraison expedier(Long id, LivraisonRequest request) {
        Commande commande = findById(id);
        verifierStatut(commande, StatutCommande.PAYEE, "expédiée");

        Livraison livraison = new Livraison();
        livraison.setCommande(commande);
        livraison.setAdresse(request.adresse());
        livraison.setVille(request.ville());
        livraison.setCodePostal(request.codePostal());
        livraison.setTransporteur(request.transporteur());
        livraison.setNumeroSuivi("TRK-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase());
        livraison.setStatut(StatutLivraison.EN_COURS);
        livraison.setDateExpedition(LocalDate.now());

        commande.setStatut(StatutCommande.EXPEDIEE);
        return livraisonRepository.save(livraison);
    }

    public Commande confirmerLivraison(Long id) {
        Commande commande = findById(id);
        verifierStatut(commande, StatutCommande.EXPEDIEE, "marquée comme livrée");
        Livraison livraison = getLivraison(id);
        livraison.setStatut(StatutLivraison.LIVREE);
        livraison.setDateLivraison(LocalDate.now());
        commande.setStatut(StatutCommande.LIVREE);
        return commande;
    }

    public Livraison getLivraison(Long commandeId) {
        findById(commandeId);
        return livraisonRepository.findByCommandeId(commandeId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune livraison pour la commande " + commandeId));
    }

    // ----- Méthodes privées -----

    /** Vérifie le stock, le décrémente, et calcule le montant total. */
    private void remplirLignes(Commande commande, List<LigneCommandeRequest> lignes) {
        BigDecimal total = BigDecimal.ZERO;
        for (LigneCommandeRequest req : lignes) {
            Produit produit = produitService.findById(req.produitId());
            if (produit.getStock() < req.quantite()) {
                throw new BusinessException("Stock insuffisant pour '" + produit.getNom()
                        + "' : demandé " + req.quantite() + ", disponible " + produit.getStock());
            }
            produit.setStock(produit.getStock() - req.quantite());

            LigneCommande ligne = new LigneCommande();
            ligne.setProduit(produit);
            ligne.setQuantite(req.quantite());
            ligne.setPrixUnitaire(produit.getPrix());
            commande.ajouterLigne(ligne);

            total = total.add(ligne.getSousTotal());
        }
        commande.setMontantTotal(total);
    }

    /** Remet en stock les quantités d'une commande (annulation / modification). */
    private void restaurerStock(Commande commande) {
        for (LigneCommande ligne : commande.getLignes()) {
            Produit produit = ligne.getProduit();
            produit.setStock(produit.getStock() + ligne.getQuantite());
        }
    }

    private void verifierStatut(Commande commande, StatutCommande attendu, String action) {
        if (commande.getStatut() != attendu) {
            throw new BusinessException("La commande ne peut pas être " + action + " : statut actuel "
                    + commande.getStatut() + ", statut requis " + attendu);
        }
    }
}
