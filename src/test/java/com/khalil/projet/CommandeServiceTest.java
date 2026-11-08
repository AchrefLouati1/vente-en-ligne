package com.khalil.projet;

import com.khalil.projet.dto.CommandeRequest;
import com.khalil.projet.dto.LigneCommandeRequest;
import com.khalil.projet.dto.PaiementRequest;
import com.khalil.projet.exception.BusinessException;
import com.khalil.projet.model.*;
import com.khalil.projet.repository.CategorieRepository;
import com.khalil.projet.repository.ClientRepository;
import com.khalil.projet.repository.ProduitRepository;
import com.khalil.projet.service.CommandeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Tests de la logique métier des commandes (base H2 en mémoire). */
@SpringBootTest
@Transactional
class CommandeServiceTest {

    @Autowired CommandeService commandeService;
    @Autowired CategorieRepository categorieRepository;
    @Autowired ProduitRepository produitRepository;
    @Autowired ClientRepository clientRepository;

    Produit produit;
    Client client;

    @BeforeEach
    void setUp() {
        Categorie cat = categorieRepository.save(new Categorie("Test catégorie", null));
        produit = produitRepository.save(new Produit("Produit test", null, new BigDecimal("100.00"), 5, cat));
        client = clientRepository.save(new Client("Client Test", "test@mail.com", null, null));
    }

    private CommandeRequest commande(int quantite) {
        return new CommandeRequest(client.getId(), List.of(new LigneCommandeRequest(produit.getId(), quantite)));
    }

    @Test
    void creerCommande_calculeLeTotal_etDecrementeLeStock() {
        Commande c = commandeService.creer(commande(2));

        assertEquals(StatutCommande.EN_ATTENTE, c.getStatut());
        assertEquals(0, new BigDecimal("200.00").compareTo(c.getMontantTotal()));
        assertEquals(3, produit.getStock());
    }

    @Test
    void creerCommande_stockInsuffisant_leveUneErreur() {
        assertThrows(BusinessException.class, () -> commandeService.creer(commande(10)));
    }

    @Test
    void payer_montantIncorrect_refuse_etCommandeResteEnAttente() {
        Commande c = commandeService.creer(commande(1));

        assertThrows(BusinessException.class,
                () -> commandeService.payer(c.getId(), new PaiementRequest(new BigDecimal("50"), MethodePaiement.CARTE)));
        assertEquals(StatutCommande.EN_ATTENTE, c.getStatut());
    }

    @Test
    void annuler_commandePayee_rembourse_etRestaureLeStock() {
        Commande c = commandeService.creer(commande(2));
        Paiement p = commandeService.payer(c.getId(), new PaiementRequest(new BigDecimal("200.00"), MethodePaiement.CARTE));

        commandeService.annuler(c.getId());

        assertEquals(StatutCommande.ANNULEE, c.getStatut());
        assertEquals(StatutPaiement.REMBOURSE, p.getStatut());
        assertEquals(5, produit.getStock());
    }
}
