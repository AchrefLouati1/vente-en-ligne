package com.khalil.projet.controller;

import com.khalil.projet.model.Client;
import com.khalil.projet.model.Commande;
import com.khalil.projet.model.Produit;
import com.khalil.projet.service.ClientService;
import com.khalil.projet.service.CommandeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final CommandeService commandeService;

    @GetMapping
    public List<Client> findAll() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    public Client findById(@PathVariable Long id) {
        return clientService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Client> create(@Valid @RequestBody Client client) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.create(client));
    }

    @PutMapping("/{id}")
    public Client update(@PathVariable Long id, @Valid @RequestBody Client client) {
        return clientService.update(id, client);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Historique des commandes du client. */
    @GetMapping("/{id}/commandes")
    public List<Commande> commandes(@PathVariable Long id) {
        return commandeService.findByClient(id);
    }

    // ----- Favoris -----

    @GetMapping("/{id}/favoris")
    public List<Produit> favoris(@PathVariable Long id) {
        return clientService.getFavoris(id);
    }

    @PostMapping("/{id}/favoris/{produitId}")
    public List<Produit> ajouterFavori(@PathVariable Long id, @PathVariable Long produitId) {
        return clientService.ajouterFavori(id, produitId);
    }

    @DeleteMapping("/{id}/favoris/{produitId}")
    public ResponseEntity<Void> retirerFavori(@PathVariable Long id, @PathVariable Long produitId) {
        clientService.retirerFavori(id, produitId);
        return ResponseEntity.noContent().build();
    }
}
