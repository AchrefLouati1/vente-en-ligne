package com.khalil.projet.controller;

import com.khalil.projet.dto.AvisRequest;
import com.khalil.projet.model.Avis;
import com.khalil.projet.service.AvisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avis")
@RequiredArgsConstructor
public class AvisController {

    private final AvisService avisService;

    @GetMapping
    public List<Avis> findAll() {
        return avisService.findAll();
    }

    @GetMapping("/{id}")
    public Avis findById(@PathVariable Long id) {
        return avisService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Avis> create(@Valid @RequestBody AvisRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(avisService.create(request));
    }

    @PutMapping("/{id}")
    public Avis update(@PathVariable Long id, @Valid @RequestBody AvisRequest request) {
        return avisService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        avisService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
