package com.khalil.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produits")
@Getter
@Setter
@NoArgsConstructor
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Column(nullable = false, length = 100)
    private String nom;

    @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
    @Column(length = 1000)
    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix doit être supérieur à 0")
    @Digits(integer = 10, fraction = 2, message = "Le prix doit avoir au plus 2 décimales")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prix;

    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Le stock ne peut pas être négatif")
    @Column(nullable = false)
    private Integer stock;

    // Plusieurs produits appartiennent à une catégorie (côté propriétaire de la relation)
    @NotNull(message = "La catégorie est obligatoire")
    @ManyToOne
    @JoinColumn(name = "categorie_id", nullable = false)
    private Categorie categorie;

    @JsonIgnore
    @OneToMany(mappedBy = "produit")
    private List<Avis> avis = new ArrayList<>();

    public Produit(String nom, String description, BigDecimal prix, Integer stock, Categorie categorie) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.stock = stock;
        this.categorie = categorie;
    }
}
