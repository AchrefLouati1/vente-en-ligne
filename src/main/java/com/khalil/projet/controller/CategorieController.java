package com.khalil.projet.controller;

import com.khalil.projet.model.Categorie;
import com.khalil.projet.model.Produit;
import com.khalil.projet.service.CategorieService;
import com.khalil.projet.service.ProduitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;
    private final ProduitService produitService;

    @GetMapping
    public List<Categorie> findAll() {
        return categorieService.findAll();
    }

    @GetMapping("/{id}")
    public Categorie findById(@PathVariable Long id) {
        return categorieService.findById(id);
    }

    @GetMapping("/{id}/produits")
    public List<Produit> findProduits(@PathVariable Long id) {
        return produitService.findByCategorie(id);
    }

    @PostMapping
    public ResponseEntity<Categorie> create(@Valid @RequestBody Categorie categorie) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categorieService.create(categorie));
    }

    @PutMapping("/{id}")
    public Categorie update(@PathVariable Long id, @Valid @RequestBody Categorie categorie) {
        return categorieService.update(id, categorie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categorieService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
