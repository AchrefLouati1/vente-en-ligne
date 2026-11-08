package com.khalil.projet.controller;

import com.khalil.projet.model.Paiement;
import com.khalil.projet.model.StatutPaiement;
import com.khalil.projet.service.PaiementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final PaiementService paiementService;

    /** GET /api/paiements  ou  GET /api/paiements?statut=REMBOURSE */
    @GetMapping
    public List<Paiement> findAll(@RequestParam(required = false) StatutPaiement statut) {
        return statut == null ? paiementService.findAll() : paiementService.findByStatut(statut);
    }

    @GetMapping("/{id}")
    public Paiement findById(@PathVariable Long id) {
        return paiementService.findById(id);
    }
}
