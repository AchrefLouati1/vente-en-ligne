package com.khalil.projet.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Avis laissé par un client sur un produit (un seul avis par client et par produit). */
@Entity
@Table(name = "avis",
        uniqueConstraints = @UniqueConstraint(columnNames = {"client_id", "produit_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Avis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer note;

    @Column(length = 500)
    private String commentaire;

    @Column(nullable = false)
    private LocalDateTime dateAvis;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "produit_id", nullable = false)
    private Produit produit;

    @PrePersist
    void onCreate() {
        dateAvis = LocalDateTime.now();
    }
}
