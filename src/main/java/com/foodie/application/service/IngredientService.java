package com.foodie.application.service;

import com.foodie.application.domain.Allergen;
import com.foodie.application.domain.Ingredient;
import com.foodie.application.repository.IngredientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Cacheable(value = "ingredients")
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "ingredients", allEntries = true)
    public Ingredient createIngredient(String ingredientName) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(ingredientName);

        return ingredientRepository.save(ingredient);
    }

    @Transactional
    @Cacheable(value = "ingredients", key = "#names")
    public Set<Ingredient> findOrCreateByNames(Set<String> names) {
        if (names == null || names.isEmpty()) {
            return Set.of();
        }
        return names.stream()
                .map(name -> ingredientRepository.findByName(name)
                        .orElseGet(() -> ingredientRepository.save(Ingredient.builder().name(name).build())))
                .collect(Collectors.toSet());
    }

    @Transactional
    @CacheEvict(value = "ingredients", allEntries = true)
    public void updateIngredientName(Integer ingredientId, String newName) {
        var ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new EntityNotFoundException("Ingredient not found with id: " + ingredientId));
        ingredient.setName(newName);
    }

    @Transactional
    public  void deleteIngredient(Integer ingredientId) {
        if (!ingredientRepository.existsById(ingredientId)) {
            throw new EntityNotFoundException("Allergen not found with id: " + ingredientId);
        }
        ingredientRepository.deleteById(ingredientId);
    }
}
