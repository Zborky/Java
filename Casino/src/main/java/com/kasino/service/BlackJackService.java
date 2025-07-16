package com.kasino.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kasino.dto.BlackJackRequestDto;
import com.kasino.dto.BlackJackResultDto;
import com.kasino.model.User;
import com.kasino.repository.UserRepository;

/**
 * Service responsible for handling BlackJack game logic including player actions,
 * score calculation, and user balance updates.
 */
@Service
public class BlackJackService {

    // Stores the state of ongoing games, identified by unique gameId
    private final Map<String, GameState> games = new HashMap<>();

    // Used to generate random cards
    private final Random random = new Random();

    // Card values and suits
    private final String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
    private final String[] suits = {"♠", "♥", "♦", "♣"};

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminGameStatsService adminGameStatsService;

    /**
     * Starts a new BlackJack game for a specific user.
     *
     * @param username the name of the user
     * @param request the request containing the bet amount
     * @return the initial state of the game
     */
    public BlackJackResultDto startGame(String username, BlackJackRequestDto request) {
        // Find user
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        double bet = request.getBetAmount();
        BlackJackResultDto result = new BlackJackResultDto();

        // Validate bet amount
        if (bet <= 0) {
            result.setMessage("Stávka musí byť väčšia ako 0");
            result.setBalance(user.getBalance());
            result.setGameOver(true);
            return result;
        }

        // Check for sufficient balance
        if (user.getBalance() < bet) {
            result.setMessage("Nedostatok prostriedkov");
            result.setBalance(user.getBalance());
            result.setGameOver(true);
            return result;
        }

        // Deduct bet from user balance
        user.setBalance(user.getBalance() - bet);
        userRepository.save(user);

        // Initialize game state
        GameState game = new GameState();
        game.bet = bet;
        game.playerCards.add(drawCard());
        game.playerCards.add(drawCard());
        game.dealerCards.add(drawCard());

        // Generate a unique game ID and store game
        String gameId = UUID.randomUUID().toString();
        games.put(gameId, game);

        // Calculate scores
        int playerScore = calculateScore(game.playerCards);
        int dealerScore = calculateScore(game.dealerCards);

        // Prepare result DTO
        result.setPlayerCards(game.playerCards);
        result.setDealerCards(game.dealerCards);
        result.setBalance(user.getBalance());
        result.setMessage("Hra začala");
        result.setGameOver(false);
        result.setPlayerScore(playerScore);
        result.setDealerScore(dealerScore);
        result.setGameId(gameId);

        return result;
    }

    /**
     * Adds a new card to the player's hand.
     *
     * @param username the name of the user
     * @param gameId the ID of the game
     * @return the updated game state
     */
    public BlackJackResultDto hit(String username, String gameId) {
        // Get user
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        GameState game = games.get(gameId);
        BlackJackResultDto result = new BlackJackResultDto();

        // Game does not exist
        if (game == null) {
            result.setMessage("Hra neexistuje");
            result.setGameOver(true);
            result.setBalance(user.getBalance());
            return result;
        }

        // Add new card to player
        game.playerCards.add(drawCard());

        // Recalculate scores
        int playerScore = calculateScore(game.playerCards);
        int dealerScore = calculateScore(game.dealerCards);

        result.setPlayerCards(game.playerCards);
        result.setDealerCards(game.dealerCards);
        result.setBalance(user.getBalance());
        result.setPlayerScore(playerScore);
        result.setDealerScore(dealerScore);
        result.setGameId(gameId);

        // Check for bust
        if (playerScore > 21) {
            result.setMessage("Prehral si");
            result.setGameOver(true);
        } else {
            result.setMessage("Karta pridaná. Skóre: " + playerScore);
            result.setGameOver(false);
        }

        return result;
    }

    /**
     * Finishes the player's turn and processes dealer's turn.
     *
     * @param username the name of the user
     * @param gameId the ID of the game
     * @return the final result of the game
     */
    public BlackJackResultDto stand(String username, String gameId) {
        // Get user
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        GameState game = games.get(gameId);
        BlackJackResultDto result = new BlackJackResultDto();

        // Game does not exist
        if (game == null) {
            result.setMessage("Hra neexistuje");
            result.setGameOver(true);
            result.setBalance(user.getBalance());
            return result;
        }

        // Dealer draws until score is at least 17
        while (calculateScore(game.dealerCards) < 17) {
            game.dealerCards.add(drawCard());
        }

        // Calculate final scores
        int playerScore = calculateScore(game.playerCards);
        int dealerScore = calculateScore(game.dealerCards);
        double winAmount = 0;
        // Determine outcome
        if (playerScore > 21) {
            result.setMessage("Prehral si");
            winAmount = 0;
        } 
        else if (dealerScore > 21 || playerScore > dealerScore) {
            winAmount = game.bet * 2;
            user.setBalance(user.getBalance() + winAmount); // Win: double money
            result.setMessage("Vyhral si! 💰");
        } 
        else if (playerScore == dealerScore) {
            winAmount = game.bet;
            user.setBalance(user.getBalance() + winAmount); // Tie: return bet
            result.setMessage("Remíza – stávka vrátená");
        } 
        else {
            winAmount = 0;
            result.setMessage("Prehral si");
        }

        userRepository.save(user);

        //Call for update stats
        double casinoProfit = game.bet - winAmount;
        adminGameStatsService.updateStatsAfterGame("BlackJack", winAmount, casinoProfit);        


        // Fill in result
        result.setPlayerCards(game.playerCards);
        result.setDealerCards(game.dealerCards);
        result.setBalance(user.getBalance());
        result.setGameOver(true);
        result.setPlayerScore(playerScore);
        result.setDealerScore(dealerScore);
        result.setGameId(gameId);

        return result;
    }

    /**
     * Draws a random card from the deck.
     *
     * @return card as string, e.g. "K♠"
     */
    private String drawCard() {
        return values[random.nextInt(values.length)] + suits[random.nextInt(suits.length)];
    }

    /**
     * Calculates the total score of the hand, handling Aces dynamically.
     *
     * @param cards list of cards
     * @return score value
     */
    private int calculateScore(List<String> cards) {
        int score = 0, aces = 0;

        for (String card : cards) {
            // Get value without suit
            String value = card.substring(0, card.length() - 1);

            if (value.equals("A")) {
                aces++;
                score += 11;
            } else if (value.matches("[KQJ]")) {
                score += 10;
            } else {
                score += Integer.parseInt(value);
            }
        }

        // Convert Aces from 11 to 1 if needed
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }

        return score;
    }

    /**
     * Inner class representing the state of one BlackJack game session.
     */
    private static class GameState {
        double bet;
        List<String> playerCards = new ArrayList<>();
        List<String> dealerCards = new ArrayList<>();
    }
}
