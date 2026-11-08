package com.khalil.projet.dto;

import java.math.BigDecimal;

/** Résultat de la requête "meilleures ventes". */
public record ProduitVenteDTO(Long produitId, String nom, Long quantiteVendue, BigDecimal chiffreAffaires) {
}
