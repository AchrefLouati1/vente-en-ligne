package com.khalil.projet.controller;

import com.khalil.projet.dto.CommandeRequest;
import com.khalil.projet.dto.LivraisonRequest;
import com.khalil.projet.dto.PaiementRequest;
import com.khalil.projet.model.Commande;
import com.khalil.projet.model.Livraison;
import com.khalil.projet.model.Paiement;
import com.khalil.projet.model.StatutCommande;
import com.khalil.projet.service.CommandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    /** GET /api/commandes  ou  GET /api/commandes?statut=PAYEE */
    @GetMapping
    public List<Commande> findAll(@RequestParam(required = false) StatutCommande statut) {
        return statut == null ? commandeService.findAll() : commandeService.findByStatut(statut);
    }

    @GetMapping("/{id}")
    public Commande findById(@PathVariable Long id) {
        return commandeService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Commande> creer(@Valid @RequestBody CommandeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandeService.creer(request));
    }

    @PutMapping("/{id}")
    public Commande modifier(@PathVariable Long id, @Valid @RequestBody CommandeRequest request) {
        return commandeService.modifier(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        commandeService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/annuler")
    public Commande annuler(@PathVariable Long id) {
        return commandeService.annuler(id);
    }

    // ----- Paiement -----

    @PostMapping("/{id}/paiement")
    public ResponseEntity<Paiement> payer(@PathVariable Long id, @Valid @RequestBody PaiementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandeService.payer(id, request));
    }

    @GetMapping("/{id}/paiement")
    public Paiement paiement(@PathVariable Long id) {
        return commandeService.getPaiement(id);
    }

    // ----- Livraison -----

    @PostMapping("/{id}/expedier")
    public ResponseEntity<Livraison> expedier(@PathVariable Long id, @Valid @RequestBody LivraisonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commandeService.expedier(id, request));
    }

    @PutMapping("/{id}/livrer")
    public Commande livrer(@PathVariable Long id) {
        return commandeService.confirmerLivraison(id);
    }

    @GetMapping("/{id}/livraison")
    public Livraison livraison(@PathVariable Long id) {
        return commandeService.getLivraison(id);
    }
}
