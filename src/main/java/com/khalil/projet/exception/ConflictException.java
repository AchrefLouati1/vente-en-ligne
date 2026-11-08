package com.khalil.projet.exception;

/** Doublon, ou suppression impossible à cause de données liées -> HTTP 409. */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
