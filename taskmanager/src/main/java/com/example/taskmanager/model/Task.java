package com.example.taskmanager.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a Task in the task manager.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    /**
     * The unique identifier of the task.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * The title of the task.
     */
    private String title;

    /**
     * Detailed description of the task.
     */
    private String description;

    /**
     * Flag indicating whether the task is completed.
     */
    private boolean completed;

    /**
     * Timestamp when the task was created.
     */
    private LocalDateTime createdAt;
}
