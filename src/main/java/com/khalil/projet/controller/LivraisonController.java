package com.khalil.projet.controller;

import com.khalil.projet.dto.LivraisonRequest;
import com.khalil.projet.model.Livraison;
import com.khalil.projet.model.StatutLivraison;
import com.khalil.projet.service.LivraisonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livraisons")
@RequiredArgsConstructor
public class LivraisonController {

    private final LivraisonService livraisonService;

    /** GET /api/livraisons  ou  GET /api/livraisons?statut=EN_COURS */
    @GetMapping
    public List<Livraison> findAll(@RequestParam(required = false) StatutLivraison statut) {
        return statut == null ? livraisonService.findAll() : livraisonService.findByStatut(statut);
    }

    @GetMapping("/{id}")
    public Livraison findById(@PathVariable Long id) {
        return livraisonService.findById(id);
    }

    @PutMapping("/{id}")
    public Livraison update(@PathVariable Long id, @Valid @RequestBody LivraisonRequest request) {
        return livraisonService.update(id, request);
    }
}
