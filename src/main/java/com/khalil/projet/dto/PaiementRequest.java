package com.khalil.projet.dto;

import com.khalil.projet.model.MethodePaiement;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaiementRequest(
        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "0.01", message = "Le montant doit être positif") BigDecimal montant,
        @NotNull(message = "La méthode de paiement est obligatoire") MethodePaiement methode) {
}
