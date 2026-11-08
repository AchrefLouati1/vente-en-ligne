package com.khalil.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entité d'association entre Commande et Produit :
 * elle porte la quantité et le prix au moment de l'achat.
 */
@Entity
@Table(name = "lignes_commande")
@Getter
@Setter
@NoArgsConstructor
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @Column(nullable = false)
    private Integer quantite;

    // Prix figé : si le prix du produit change plus tard, la commande ne change pas
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prixUnitaire;

    /** Calculé, non stocké en base (exposé en JSON sous "sousTotal"). */
    public BigDecimal getSousTotal() {
        return prixUnitaire.multiply(BigDecimal.valueOf(quantite));
    }
}
