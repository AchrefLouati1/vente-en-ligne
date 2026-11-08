package com.khalil.projet.service;

import com.khalil.projet.exception.ConflictException;
import com.khalil.projet.exception.ResourceNotFoundException;
import com.khalil.projet.model.Client;
import com.khalil.projet.model.Produit;
import com.khalil.projet.repository.ClientRepository;
import com.khalil.projet.repository.CommandeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final CommandeRepository commandeRepository;
    private final ProduitService produitService;

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    public Client create(Client client) {
        if (clientRepository.existsByEmailIgnoreCase(client.getEmail())) {
            throw new ConflictException("Un client avec l'email " + client.getEmail() + " existe déjà");
        }
        client.setId(null);
        return clientRepository.save(client);
    }

    public Client update(Long id, Client data) {
        Client client = findById(id);
        if (clientRepository.existsByEmailIgnoreCaseAndIdNot(data.getEmail(), id)) {
            throw new ConflictException("L'email " + data.getEmail() + " est déjà utilisé par un autre client");
        }
        client.setNom(data.getNom());
        client.setEmail(data.getEmail());
        client.setTelephone(data.getTelephone());
        client.setAdresse(data.getAdresse());
        return client;
    }

    public void delete(Long id) {
        Client client = findById(id);
        if (commandeRepository.existsByClientId(id)) {
            throw new ConflictException("Impossible de supprimer le client : il possède des commandes");
        }
        clientRepository.delete(client);
    }

    // ----- Favoris (relation ManyToMany) -----

    public List<Produit> getFavoris(Long clientId) {
        return new ArrayList<>(findById(clientId).getFavoris());
    }

    public List<Produit> ajouterFavori(Long clientId, Long produitId) {
        Client client = findById(clientId);
        Produit produit = produitService.findById(produitId);
        if (!client.getFavoris().add(produit)) {
            throw new ConflictException("Ce produit est déjà dans les favoris du client");
        }
        return new ArrayList<>(client.getFavoris());
    }

    public void retirerFavori(Long clientId, Long produitId) {
        Client client = findById(clientId);
        Produit produit = produitService.findById(produitId);
        if (!client.getFavoris().remove(produit)) {
            throw new ResourceNotFoundException("Le produit " + produitId + " n'est pas dans les favoris du client");
        }
    }
}
