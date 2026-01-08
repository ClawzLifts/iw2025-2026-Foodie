package com.foodie.application.service;

import com.foodie.application.domain.Allergen;
import com.foodie.application.repository.AllergenRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AllergenService {
    private final AllergenRepository allergenRepository;

    public AllergenService(AllergenRepository allergenRepository) {
        this.allergenRepository = allergenRepository;
    }

    @Cacheable(value = "allergens")
    public List<Allergen> getAllAllergens() {
        return allergenRepository.findAll();
    }

    @Cacheable(value = "allergens", key = "#id")
    public Allergen getAllergenById(Integer id) {
        return allergenRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Allergen not found with id: " + id));
    }

    @Transactional
    @Cacheable(value = "allergens", key = "#names")
    public Set<Allergen> findOrCreateByNames(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return Set.of();
        }
        return names.stream()
                .map(name -> allergenRepository.findByName(name)
                        .orElseGet(() -> allergenRepository.save(Allergen.builder().name(name).build())))
                .collect(Collectors.toSet());
    }

    @Transactional
    @CacheEvict(value = "allergens", allEntries = true)
    public Allergen createAllergen(String allergenName) {
        Allergen allergen = new Allergen();
        allergen.setName(allergenName);

        return allergenRepository.save(allergen);
    }

    @Transactional
    @CacheEvict(value = "allergens", allEntries = true)
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
