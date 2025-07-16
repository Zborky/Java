package com.kasino.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kasino.model.GameStats;

@Repository
public interface GameStatsRepository extends JpaRepository<GameStats, String> {
    // Tu môžeš pridať vlastné metódy, ak budeš potrebovať
}
