package com.khalil.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/** Une catégorie regroupe plusieurs produits (OneToMany). */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la catégorie est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    @Column(nullable = false, unique = true, length = 50)
    private String nom;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;

    // Côté inverse de la relation : ignoré en JSON pour éviter une boucle infinie
    @JsonIgnore
    @OneToMany(mappedBy = "categorie")
    private List<Produit> produits = new ArrayList<>();

    public Categorie(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }
}
