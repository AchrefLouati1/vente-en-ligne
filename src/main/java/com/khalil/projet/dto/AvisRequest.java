package com.khalil.projet.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvisRequest(
        @NotNull(message = "clientId est obligatoire") Long clientId,
        @NotNull(message = "produitId est obligatoire") Long produitId,
        @NotNull(message = "La note est obligatoire")
        @Min(value = 1, message = "La note minimale est 1")
        @Max(value = 5, message = "La note maximale est 5") Integer note,
        @Size(max = 500, message = "Le commentaire ne doit pas dépasser 500 caractères") String commentaire) {
}
