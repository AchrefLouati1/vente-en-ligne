package com.khalil.projet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LivraisonRequest(
        @NotBlank(message = "L'adresse est obligatoire") @Size(max = 255) String adresse,
        @NotBlank(message = "La ville est obligatoire") @Size(max = 100) String ville,
        @NotBlank(message = "Le code postal est obligatoire")
        @Pattern(regexp = "^[0-9]{4,5}$", message = "Le code postal doit contenir 4 ou 5 chiffres") String codePostal,
        @NotBlank(message = "Le transporteur est obligatoire") @Size(max = 50) String transporteur) {
}
