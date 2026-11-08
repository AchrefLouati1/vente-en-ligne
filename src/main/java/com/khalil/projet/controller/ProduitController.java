package com.khalil.projet.controller;

import com.khalil.projet.dto.NoteMoyenneDTO;
import com.khalil.projet.model.Avis;
import com.khalil.projet.model.Produit;
import com.khalil.projet.service.AvisService;
import com.khalil.projet.service.ProduitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;
    private final AvisService avisService;

    @GetMapping
    public List<Produit> findAll() {
        return produitService.findAll();
    }

    @GetMapping("/{id}")
    public Produit findById(@PathVariable Long id) {
        return produitService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Produit> create(@Valid @RequestBody Produit produit) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produitService.create(produit));
    }

    @PutMapping("/{id}")
    public Produit update(@PathVariable Long id, @Valid @RequestBody Produit produit) {
        return produitService.update(id, produit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        produitService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ----- Recherches -----

    /** GET /api/produits/recherche?nom=pc */
    @GetMapping("/recherche")
    public List<Produit> rechercher(@RequestParam String nom) {
        return produitService.rechercherParNom(nom);
    }

    /** GET /api/produits/prix?min=10&max=500 */
    @GetMapping("/prix")
    public List<Produit> parPrix(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        return produitService.findByPrix(min, max);
    }

    /** GET /api/produits/alerte-stock?seuil=5 */
    @GetMapping("/alerte-stock")
    public List<Produit> alerteStock(@RequestParam(defaultValue = "5") int seuil) {
        return produitService.alerteStock(seuil);
    }

    // ----- Avis du produit -----

    @GetMapping("/{id}/avis")
    public List<Avis> avis(@PathVariable Long id) {
        return avisService.findByProduit(id);
    }

    @GetMapping("/{id}/note-moyenne")
    public NoteMoyenneDTO noteMoyenne(@PathVariable Long id) {
        return avisService.noteMoyenne(id);
    }
}
