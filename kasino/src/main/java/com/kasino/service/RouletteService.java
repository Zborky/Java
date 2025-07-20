package com.kasino.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kasino.dto.RouletteRequestDto;
import com.kasino.dto.RouletteResultDto;
import com.kasino.model.User;
import com.kasino.repository.UserRepository;

/**
 * Service class responsible for handling the Roulette game logic,
 * including starting the game, processing bets, determining wins,
 * and updating user balances.
 */
@Service
public class RouletteService {

    // Random number generator used for spinning the roulette wheel
    private final Random random = new Random();

    // Stores ongoing games if needed for future use (currently empty)
    private final Map<String, GameState> games = new HashMap<>();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminGameStatsService adminGameStatsService;

    /**
     * Starts a Roulette game by processing the user's bet, spinning the wheel,
     * determining the result, and updating the user's balance accordingly.
     *
     * @param username the username of the player
     * @param request the bet details including bet amount, type, and chosen number
     * @return result object containing game outcome and updated balance
     */
    public RouletteResultDto startGame(String username, RouletteRequestDto request) {
        // Find user by username or throw if not found
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("Používateľ '" + username + "' neexistuje v databáze."));

        double betAmount = request.getBetAmount();
        String betType = request.getBetType();
        Integer chosenNumber = request.getChosenNumber();

        RouletteResultDto result = new RouletteResultDto();

        // Validate bet amount
        if (betAmount <= 0) {
            result.setMessage("Bet must be greater than 0");
            result.setBalance(user.getBalance());
            result.setGameOver(true);
            return result;
        }

        // Check if user has sufficient balance
        if (user.getBalance() < betAmount) {
            result.setMessage("Insufficient funds");
            result.setBalance(user.getBalance());
            result.setGameOver(true);
            return result;
        }

        // Deduct the bet amount from user's balance
        user.setBalance(user.getBalance() - betAmount);
        userRepository.save(user);

        // Spin the roulette wheel to get winning number and color
        int winningNumber = random.nextInt(37); // numbers from 0 to 36 inclusive
        String winningColor = getColor(winningNumber);

        boolean win = false;
        double amountWon = 0;

        // Determine if the user won based on bet type and outcome
        if ("RED".equalsIgnoreCase(betType) || "BLACK".equalsIgnoreCase(betType)) {
            // If betting on color, check if colors match
            if (winningColor.equalsIgnoreCase(betType)) {
                win = true;
                amountWon = betAmount * 2; // payout for color bet is 2x
            }
        } else if ("NUMBER".equalsIgnoreCase(betType)) {
            // If betting on a specific number, check if it matches winning number
            if (chosenNumber != null && chosenNumber == winningNumber) {
                win = true;
                amountWon = betAmount * 36; // payout for number bet is 36x
            }
        }

        // If user won, update balance accordingly
        if (win) {
            user.setBalance(user.getBalance() + amountWon);
            result.setMessage("You won! 💰");
        } else {
            result.setMessage("You lost");
        }

        userRepository.save(user);
        double casinoProfit = betAmount - amountWon;
        adminGameStatsService.updateStatsAfterGame("Roulette", amountWon, casinoProfit);
        // Generate unique game ID and store empty game state (for possible future use)
        String gameId = UUID.randomUUID().toString();
        GameState state = new GameState();
        games.put(gameId, state);

        // Set response DTO fields
        result.setBalance(user.getBalance());
        result.setGameOver(true);
        result.setGameId(gameId);
        result.setWin(win);
        result.setAmountWon(win ? amountWon : 0);
        result.setWinningNumber(winningNumber);
        result.setWinningColor(winningColor);

        return result;
    }

    /**
     * Determines the color of the roulette pocket given its number.
     *
     * @param number the roulette pocket number (0-36)
     * @return "RED", "BLACK", or "GREEN" for zero
     */
    private String getColor(int number) {
        if (number == 0) return "GREEN";

        // List of red numbers on a roulette wheel
        List<Integer> redNumbers = Arrays.asList(
            1, 3, 5, 7, 9, 12, 14, 16, 18,
            19, 21, 23, 25, 27, 30, 32, 34, 36
        );

        // Return RED if number is in redNumbers, else BLACK
        return redNumbers.contains(number) ? "RED" : "BLACK";
    }

    /**
     * Inner class to represent the state of a Roulette game session.
     * Currently empty but can be extended to store game history or state.
     */
    private static class GameState {
        // Empty for now - placeholder for future enhancements
    }
}
