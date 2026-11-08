package com.khalil.projet.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Pattern(regexp = "^[0-9+ ]{8,15}$", message = "Le téléphone doit contenir 8 à 15 chiffres")
    private String telephone;

    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    private String adresse;

    // Remplie automatiquement, non modifiable depuis le JSON
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(nullable = false, updatable = false)
    private LocalDate dateInscription;

    // Un client passe plusieurs commandes (OneToMany)
    @JsonIgnore
    @OneToMany(mappedBy = "client")
    private List<Commande> commandes = new ArrayList<>();

    // Liste de souhaits : un client a plusieurs produits favoris,
    // et un produit peut être le favori de plusieurs clients (ManyToMany)
    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "client_favoris",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "produit_id"))
    private Set<Produit> favoris = new HashSet<>();

    public Client(String nom, String email, String telephone, String adresse) {
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
        this.adresse = adresse;
    }

    @PrePersist
    void onCreate() {
        if (dateInscription == null) {
            dateInscription = LocalDate.now();
        }
    }
}
