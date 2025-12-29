package com.foodie.application.examplefeature;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Task createTask(String description, java.time.LocalDate dueDate) {
        if (description == null || description.length() > Task.DESCRIPTION_MAX_LENGTH) {
            throw new IllegalArgumentException("Descripción inválida");
        }

        Task t = Task.builder()
                .description(description)
                .dueDate(dueDate)
                .creationDate(Instant.now())
                .build();

        return repository.save(t);
    }

    @Transactional(readOnly = true)
    public List<Task> list(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }
}
