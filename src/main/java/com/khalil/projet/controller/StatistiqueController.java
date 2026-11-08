package com.khalil.projet.controller;

import com.khalil.projet.dto.ChiffreAffairesDTO;
import com.khalil.projet.dto.ClientDepenseDTO;
import com.khalil.projet.dto.ProduitVenteDTO;
import com.khalil.projet.service.StatistiqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistiques")
@RequiredArgsConstructor
public class StatistiqueController {

    private final StatistiqueService statistiqueService;

    /** GET /api/statistiques/meilleures-ventes?limit=5 */
    @GetMapping("/meilleures-ventes")
    public List<ProduitVenteDTO> meilleuresVentes(@RequestParam(defaultValue = "5") int limit) {
        return statistiqueService.meilleuresVentes(limit);
    }

    /** GET /api/statistiques/meilleurs-clients?limit=5 */
    @GetMapping("/meilleurs-clients")
    public List<ClientDepenseDTO> meilleursClients(@RequestParam(defaultValue = "5") int limit) {
        return statistiqueService.meilleursClients(limit);
    }

    /** GET /api/statistiques/chiffre-affaires?debut=2026-01-01&fin=2026-12-31 */
    @GetMapping("/chiffre-affaires")
    public ChiffreAffairesDTO chiffreAffaires(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return statistiqueService.chiffreAffaires(debut, fin);
    }
}
