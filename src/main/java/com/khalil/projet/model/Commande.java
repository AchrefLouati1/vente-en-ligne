package com.khalil.projet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "commandes")
@Getter
@Setter
@NoArgsConstructor
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String reference;

    @Column(nullable = false)
    private LocalDateTime dateCommande;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutCommande statut;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montantTotal = BigDecimal.ZERO;

    // Plusieurs commandes pour un client (ManyToOne)
    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    // Les lignes sont créées/supprimées avec la commande (cascade + orphanRemoval)
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneCommande> lignes = new ArrayList<>();

    public void ajouterLigne(LigneCommande ligne) {
        ligne.setCommande(this);
        lignes.add(ligne);
    }

    @PrePersist
    void onCreate() {
        if (dateCommande == null) {
            dateCommande = LocalDateTime.now();
        }
        if (reference == null) {
            reference = "CMD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
