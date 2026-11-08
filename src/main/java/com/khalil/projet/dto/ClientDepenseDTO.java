package com.khalil.projet.dto;

import java.math.BigDecimal;

/** Résultat de la requête "meilleurs clients". */
public record ClientDepenseDTO(Long clientId, String nom, String email, Long nombreCommandes, BigDecimal totalDepense) {
}
