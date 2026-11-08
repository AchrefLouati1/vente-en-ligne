package com.khalil.projet.service;

import com.khalil.projet.exception.BusinessException;
import com.khalil.projet.exception.ConflictException;
import com.khalil.projet.exception.ResourceNotFoundException;
import com.khalil.projet.model.Categorie;
import com.khalil.projet.model.Produit;
import com.khalil.projet.repository.ClientRepository;
import com.khalil.projet.repository.LigneCommandeRepository;
import com.khalil.projet.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final CategorieService categorieService;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final ClientRepository clientRepository;

    public List<Produit> findAll() {
        return produitRepository.findAll();
    }

    public Produit findById(Long id) {
        return produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
    }

    public Produit create(Produit produit) {
        produit.setId(null);
        produit.setCategorie(resoudreCategorie(produit.getCategorie()));
        return produitRepository.save(produit);
    }

    public Produit update(Long id, Produit data) {
        Produit produit = findById(id);
        produit.setNom(data.getNom());
        produit.setDescription(data.getDescription());
        produit.setPrix(data.getPrix());
        produit.setStock(data.getStock());
        produit.setCategorie(resoudreCategorie(data.getCategorie()));
        return produit;
    }

    public void delete(Long id) {
        Produit produit = findById(id);
        if (ligneCommandeRepository.existsByProduitId(id)) {
            throw new ConflictException("Impossible de supprimer le produit : il apparaît dans des commandes");
        }
        // On retire d'abord le produit des listes de favoris (table de jointure ManyToMany)
        clientRepository.findByFavorisId(id).forEach(c -> c.getFavoris().remove(produit));
        produitRepository.delete(produit);
    }

    public List<Produit> rechercherParNom(String nom) {
        return produitRepository.findByNomContainingIgnoreCase(nom);
    }

    public List<Produit> findByCategorie(Long categorieId) {
        categorieService.findById(categorieId); // 404 si la catégorie n'existe pas
        return produitRepository.findByCategorieId(categorieId);
    }

    public List<Produit> findByPrix(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            throw new BusinessException("Le prix minimum doit être inférieur ou égal au prix maximum");
        }
        return produitRepository.findByPrixBetweenOrderByPrixAsc(min, max);
    }

    public List<Produit> alerteStock(int seuil) {
        if (seuil < 0) {
            throw new BusinessException("Le seuil doit être positif");
        }
        return produitRepository.findByStockLessThanEqualOrderByStockAsc(seuil);
    }

    /** Le JSON envoie seulement {"categorie": {"id": 1}} : on charge la vraie catégorie. */
    private Categorie resoudreCategorie(Categorie categorie) {
        if (categorie == null || categorie.getId() == null) {
            throw new BusinessException("categorie.id est obligatoire");
        }
        return categorieService.findById(categorie.getId());
    }
}
