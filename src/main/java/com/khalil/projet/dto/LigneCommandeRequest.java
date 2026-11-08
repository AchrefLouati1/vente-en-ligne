package com.khalil.projet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LigneCommandeRequest(
        @NotNull(message = "produitId est obligatoire") Long produitId,
        @NotNull(message = "La quantité est obligatoire")
        @Min(value = 1, message = "La quantité doit être au moins 1") Integer quantite) {
}
