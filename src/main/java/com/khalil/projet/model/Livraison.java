package com.khalil.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "livraisons")
@Getter
@Setter
@NoArgsConstructor
public class Livraison {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Une commande a au plus une livraison (OneToOne)
    @JsonIgnoreProperties({"lignes"})
    @OneToOne(optional = false)
    @JoinColumn(name = "commande_id", nullable = false, unique = true)
    private Commande commande;

    @Column(nullable = false)
    private String adresse;

    @Column(nullable = false, length = 100)
    private String ville;

    @Column(nullable = false, length = 10)
    private String codePostal;

    @Column(nullable = false, length = 50)
    private String transporteur;

    @Column(nullable = false, unique = true, length = 30)
    private String numeroSuivi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutLivraison statut;

    private LocalDate dateExpedition;

    private LocalDate dateLivraison;
}
