package com.kasino.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kasino.dto.DiceGameRequestDto;
import com.kasino.dto.DiceGameResultDto;
import com.kasino.model.User;
import com.kasino.repository.UserRepository;

/**
 * Service class for handling dice game logic.
 */
@Service
public class DiceService {

    // Random number generator for dice rolls
    private final Random random = new Random();

    // Map to hold ongoing games (currently unused, reserved for future use)
    private final Map<String, GameState> games = new HashMap<>();

    @Autowired
    private UserRepository userRepository;

    /**
     * Starts a new dice game for a given user.
     *
     * @param username the username of the player
     * @param request  the game request containing the bet amount
     * @return a result object containing the outcome of the game
     */
    public DiceGameResultDto startGame(String username, DiceGameRequestDto request) {
        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User '" + username + "' does not exist."));

        double betAmount = request.getBetAmount();
        DiceGameResultDto result = new DiceGameResultDto();

        // Validate bet amount
        if (betAmount <= 0) {
            result.setMessage("Bet must be greater than 0.");
            result.setBalance(user.getBalance());
            result.setGameOver(true);
            return result;
        }

        // Check if user has enough balance
        if (user.getBalance() < betAmount) {
            result.setMessage("Insufficient funds.");
            result.setBalance(user.getBalance());
            result.setGameOver(true);
            return result;
        }

        // Deduct bet from user's balance before rolling
        user.setBalance(user.getBalance() - betAmount);

        // Roll two dice (values between 1 and 6)
        int dice1 = random.nextInt(6) + 1;
        int dice2 = random.nextInt(6) + 1;
        int total = dice1 + dice2;

        boolean win = false;
        boolean draw = false;
        double amountWon = 0;
        String message;

        // Determine game outcome based on total
        if (total == 7 || total == 11) {
            win = true;
            amountWon = betAmount * 2;
            message = "You rolled " + total + ". You win! 🎲";
        } else if (total == 2 || total == 3 || total == 12) {
            win = false;
            amountWon = 0;
            message = "You rolled " + total + ". You lose.";
        } else {
            draw = true;
            amountWon = betAmount; // player gets the bet back
            message = "You rolled " + total + ". It's a draw.";
        }

        // Update user balance with winnings or returned bet
        user.setBalance(user.getBalance() + amountWon);

        // Save updated user balance to the database
        userRepository.save(user);

        // Create a unique game ID (currently unused, future feature)
        String gameId = UUID.randomUUID().toString();
        games.put(gameId, new GameState());

        // Populate result DTO
        result.setMessage(message);
        result.setBalance(user.getBalance());
        result.setGameOver(true);
        result.setGameId(gameId);
        result.setWin(win);
        result.setAmountWon(win ? amountWon : (draw ? 0 : 0)); // amount shown only on win
        result.setDice1(dice1);
        result.setDice2(dice2);
        result.setTotal(total);

        return result;
    }

    /**
     * Represents the state of a game.
     * Reserved for future expansion.
     */
    private static class GameState {
        // Future enhancement
    }
}
