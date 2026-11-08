package com.khalil.projet.exception;

/** Ressource introuvable -> HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String ressource, Long id) {
        super(ressource + " introuvable avec l'id " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
