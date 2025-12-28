package com.foodie.application.service;

import com.foodie.application.domain.Establishment;
import com.foodie.application.repository.EstablishmentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class EstablishmentService {
    private final EstablishmentRepository establishmentRepository;

    public EstablishmentService(EstablishmentRepository establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }

    public Establishment getEstablishment() {
        return establishmentRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Establishment not found with id: 1"));
    }

    @Transactional
    public void updateEstablishmentName(String newName) {
        var establishment = establishmentRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Establishment not found with id: 1"));

        establishment.setName(newName);
    }

    @Transactional
    public void updateEstablishmentAddress(String newAddress) {
        var establishment = establishmentRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Establishment not found with id: 1"));

        establishment.setAddress(newAddress);
    }

    @Transactional
    public void updateEstablishmentPhone(String newPhone) {
        var establishment = establishmentRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Establishment not found with id: 1"));

        establishment.setPhone(newPhone);
    }

    @Transactional
    public void updateEstablishmentDescription(String newDescription) {
        var establishment = establishmentRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Establishment not found with id: 1"));

        establishment.setDescription(newDescription);
    }

    @Transactional
    public void updateEstablishmentOpeningTime(LocalTime newOpeningTime) {
        var establishment = establishmentRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Establishment not found with id: 1"));

        establishment.setOpeningTime(newOpeningTime);
    }

    @Transactional
    public void updateEstablishmentClosingTime(LocalTime newClosingTime) {
        var establishment = establishmentRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Establishment not found with id: 1"));

        establishment.setClosingTime(newClosingTime);
    }
}
