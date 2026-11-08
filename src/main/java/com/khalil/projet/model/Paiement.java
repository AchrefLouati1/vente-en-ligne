package com.khalil.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paiements")
@Getter
@Setter
@NoArgsConstructor
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Une commande a au plus un paiement (OneToOne)
    @JsonIgnoreProperties({"lignes", "client"})
    @OneToOne(optional = false)
    @JoinColumn(name = "commande_id", nullable = false, unique = true)
    private Commande commande;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MethodePaiement methode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutPaiement statut;

    @Column(nullable = false)
    private LocalDateTime datePaiement;
}
