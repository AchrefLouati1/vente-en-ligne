package com.khalil.projet.config;

import com.khalil.projet.model.Categorie;
import com.khalil.projet.model.Client;
import com.khalil.projet.model.Produit;
import com.khalil.projet.repository.CategorieRepository;
import com.khalil.projet.repository.ClientRepository;
import com.khalil.projet.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/** Insère quelques données de démonstration au premier démarrage (base vide). */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategorieRepository categorieRepository;
    private final ProduitRepository produitRepository;
    private final ClientRepository clientRepository;

    @Override
    public void run(String... args) {
        if (categorieRepository.count() > 0) {
            return;
        }

        Categorie informatique = new Categorie("Informatique", "Ordinateurs et accessoires");
        Categorie telephonie = new Categorie("Téléphonie", "Smartphones et accessoires");
        Categorie maison = new Categorie("Maison", "Électroménager et décoration");
        categorieRepository.saveAll(List.of(informatique, telephonie, maison));

        produitRepository.saveAll(List.of(
                new Produit("PC Portable Lenovo", "15 pouces, 16 Go RAM, SSD 512 Go", new BigDecimal("1899.00"), 10, informatique),
                new Produit("Souris sans fil", "Souris Logitech M185", new BigDecimal("45.50"), 50, informatique),
                new Produit("Clavier mécanique", "Clavier AZERTY rétroéclairé", new BigDecimal("120.00"), 3, informatique),
                new Produit("Samsung Galaxy A55", "128 Go, 5G", new BigDecimal("1299.00"), 15, telephonie),
                new Produit("Écouteurs Bluetooth", "Autonomie 24h", new BigDecimal("89.90"), 4, telephonie),
                new Produit("Cafetière", "Cafetière à filtre 1.2 L", new BigDecimal("75.00"), 20, maison)));

        clientRepository.saveAll(List.of(
                new Client("Ahmed Ben Ali", "ahmed.benali@mail.com", "+216 22111222", "12 rue de Carthage, Tunis"),
                new Client("Sarra Trabelsi", "sarra.trabelsi@mail.com", "+216 55333444", "5 avenue Habib Bourguiba, Sfax"),
                new Client("Youssef Gharbi", "youssef.gharbi@mail.com", "+216 98555666", "20 rue de Sousse, Sousse")));
    }
}
