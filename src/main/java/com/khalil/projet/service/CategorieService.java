package com.khalil.projet.service;

import com.khalil.projet.exception.ConflictException;
import com.khalil.projet.exception.ResourceNotFoundException;
import com.khalil.projet.model.Categorie;
import com.khalil.projet.repository.CategorieRepository;
import com.khalil.projet.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final ProduitRepository produitRepository;

    public List<Categorie> findAll() {
        return categorieRepository.findAll();
    }

    public Categorie findById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie", id));
    }

    public Categorie create(Categorie categorie) {
        if (categorieRepository.existsByNomIgnoreCase(categorie.getNom())) {
            throw new ConflictException("Une catégorie nommée '" + categorie.getNom() + "' existe déjà");
        }
        categorie.setId(null);
        return categorieRepository.save(categorie);
    }

    public Categorie update(Long id, Categorie data) {
        Categorie categorie = findById(id);
        if (categorieRepository.existsByNomIgnoreCaseAndIdNot(data.getNom(), id)) {
            throw new ConflictException("Une catégorie nommée '" + data.getNom() + "' existe déjà");
        }
        categorie.setNom(data.getNom());
        categorie.setDescription(data.getDescription());
        return categorie; // sauvegardé automatiquement à la fin de la transaction (dirty checking)
    }

    public void delete(Long id) {
        Categorie categorie = findById(id);
        if (produitRepository.existsByCategorieId(id)) {
            throw new ConflictException("Impossible de supprimer la catégorie : elle contient encore des produits");
        }
        categorieRepository.delete(categorie);
    }
}
