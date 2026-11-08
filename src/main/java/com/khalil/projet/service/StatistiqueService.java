package com.khalil.projet.service;

import com.khalil.projet.dto.ChiffreAffairesDTO;
import com.khalil.projet.dto.ClientDepenseDTO;
import com.khalil.projet.dto.ProduitVenteDTO;
import com.khalil.projet.exception.BusinessException;
import com.khalil.projet.model.StatutCommande;
import com.khalil.projet.repository.ClientRepository;
import com.khalil.projet.repository.CommandeRepository;
import com.khalil.projet.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Tableau de bord : utilise les requêtes personnalisées des repositories. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatistiqueService {

    /** Commandes réellement encaissées (on exclut EN_ATTENTE et ANNULEE). */
    private static final Set<StatutCommande> STATUTS_VALIDES =
            EnumSet.of(StatutCommande.PAYEE, StatutCommande.EXPEDIEE, StatutCommande.LIVREE);

    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;
    private final ClientRepository clientRepository;

    public List<ProduitVenteDTO> meilleuresVentes(int limit) {
        verifierLimite(limit);
        return produitRepository.findMeilleuresVentes(STATUTS_VALIDES, PageRequest.of(0, limit));
    }

    public List<ClientDepenseDTO> meilleursClients(int limit) {
        verifierLimite(limit);
        return clientRepository.findMeilleursClients(STATUTS_VALIDES, PageRequest.of(0, limit));
    }

    public ChiffreAffairesDTO chiffreAffaires(LocalDate debut, LocalDate fin) {
        if (debut.isAfter(fin)) {
            throw new BusinessException("La date de début doit être avant la date de fin");
        }
        LocalDateTime d = debut.atStartOfDay();
        LocalDateTime f = fin.atTime(LocalTime.MAX);
        BigDecimal total = commandeRepository.calculerChiffreAffaires(STATUTS_VALIDES, d, f);
        long nombre = commandeRepository.countByStatutInAndDateCommandeBetween(STATUTS_VALIDES, d, f);
        return new ChiffreAffairesDTO(debut, fin, nombre, total == null ? BigDecimal.ZERO : total);
    }

    private void verifierLimite(int limit) {
        if (limit < 1 || limit > 100) {
            throw new BusinessException("Le paramètre limit doit être entre 1 et 100");
        }
    }
}
