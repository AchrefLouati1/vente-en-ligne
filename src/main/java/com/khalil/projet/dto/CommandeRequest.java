package com.khalil.projet.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/** Corps JSON pour créer ou modifier une commande. */
public record CommandeRequest(
        @NotNull(message = "clientId est obligatoire") Long clientId,
        @NotEmpty(message = "La commande doit contenir au moins une ligne")
        @Valid List<LigneCommandeRequest> lignes) {
}
