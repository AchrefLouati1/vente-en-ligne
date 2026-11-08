package com.khalil.projet.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ChiffreAffairesDTO(LocalDate debut, LocalDate fin, long nombreCommandes, BigDecimal chiffreAffaires) {
}
