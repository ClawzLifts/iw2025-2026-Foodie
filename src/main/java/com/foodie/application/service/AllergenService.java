package com.foodie.application.service;

import com.foodie.application.domain.Allergen;
import com.foodie.application.repository.AllergenRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AllergenService {
    private AllergenRepository allergenRepository;

    public AllergenService(AllergenRepository allergenRepository) {
        this.allergenRepository = allergenRepository;
    }

    public List<Allergen> getAllAllergens() {
        return allergenRepository.findAll();
    }

    public Allergen getAllergenById(Integer id) {
        return allergenRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Allergen not found with id: " + id));
    }

    @Transactional
    public Allergen addAllergen(String allergenName) {
        Allergen allergen = new Allergen();
        allergen.setName(allergenName);

        return allergenRepository.save(allergen);
    }

    @Transactional
    public void updateAllergenName(Integer allergenId, String newName) {
        var allergen = allergenRepository.findById(allergenId)
                .orElseThrow(() -> new EntityNotFoundException("Allergen not found with id: " + allergenId));
        allergen.setName(newName);
    }

    @Transactional
    public  void deleteAllergen(Integer allergenId) {
        if (!allergenRepository.existsById(allergenId)) {
            throw new EntityNotFoundException("Allergen not found with id: " + allergenId);
        }
        allergenRepository.deleteById(allergenId);
    }
}
