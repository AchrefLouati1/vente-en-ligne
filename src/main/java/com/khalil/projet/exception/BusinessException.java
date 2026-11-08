package com.khalil.projet.exception;

/** Règle métier non respectée (stock insuffisant, mauvais statut, montant incorrect...) -> HTTP 400. */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
