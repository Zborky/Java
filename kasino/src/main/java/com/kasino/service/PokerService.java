package com.kasino.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kasino.dto.PokerRequestDto;
import com.kasino.dto.PokerResultDto;
import com.kasino.model.PokerGameState;
import com.kasino.model.User;
import com.kasino.repository.UserRepository;
import com.kasino.service.HandEvaluator.PokerHand;

@Service
public class PokerService {

    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();

    // Suits used in poker cards
    private final String[] SUITS = {"♠", "♥", "♦", "♣"};

    // Ranks used in poker cards
    private final String[] RANKS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

    // In-memory map of ongoing poker games identified by game ID
    private final Map<String, PokerGameState> games = new HashMap<>();

    /**
     * Starts a new poker game for a given user and bet amount.
     */
    public PokerResultDto startGame(String username, PokerRequestDto request) {
        // Retrieve user from repository
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User " + username + " does not exist"));

        double bet = request.getBetAmount();

        // Validate bet amount and user balance
        if (bet <= 0 || user.getBalance() < bet) {
            return new PokerResultDto("Invalid bet", user.getBalance(), true);
        }

        // Deduct bet from user's balance
        user.setBalance(user.getBalance() - bet);

        // Create and shuffle a new deck
        List<String> deck = createShuffledDeck();

        // Deal two cards to player and bot
        List<String> playerCards = drawCards(deck, 2);
        List<String> botCards = drawCards(deck, 2);

        // Initialize game state
        PokerGameState state = new PokerGameState();
        state.setUsername(username);
        state.setDeck(deck);
        state.setPlayerCards(playerCards);
        state.setBotCards(botCards);
        state.setPot(bet * 2); // total pot from both players
        state.setBetAmount(bet);
        state.setCurrentBet(bet);

        // Generate a unique ID for this game
        String gameId = UUID.randomUUID().toString();

        // Save game state to memory
        games.put(gameId, state);

        // Save updated user balance
        userRepository.save(user);

        // Prepare and return response DTO
        PokerResultDto result = new PokerResultDto();
        result.setGameId(gameId);
        result.setPlayerCards(playerCards);
        result.setCommunityCards(Collections.emptyList());
        result.setBalance(user.getBalance());
        result.setMessage("Game started: Pre-flop");
        result.setGameOver(false);
        result.setPot(state.getPot());

        return result;
    }

    /**
     * Processes a player's action (CALL, RAISE, FOLD) during the game.
     */
    public PokerResultDto playerAction(String username, String gameId, String action, double raiseAmount) {
        // Get game state by ID
        PokerGameState game = games.get(gameId);

        // Validate game state
        if (game == null || game.isPlayerFolded() || game.getStage() == PokerGameState.GameStage.SHOWDOWN) {
            return new PokerResultDto("Invalid game", 0, true);
        }

        // Retrieve user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User " + username + " does not exist"));

        // Handle player's action
        switch (action.toUpperCase()) {
            case "CALL" -> {
                // No changes in balance, proceed to next stage
            }
            case "RAISE" -> {
                // Validate raise amount and check user balance
                if (raiseAmount <= 0 || user.getBalance() < raiseAmount) {
                    return new PokerResultDto("Insufficient funds to raise", user.getBalance(), false);
                }

                // Apply raise: update current bet and pot, deduct from balance
                game.setCurrentBet(raiseAmount);
                game.setPot(game.getPot() + raiseAmount);
                user.setBalance(user.getBalance() - raiseAmount);
                userRepository.save(user);

                // Bot decides whether to call or fold
                boolean botCalls = botShouldCall(game, raiseAmount);

                if (botCalls) {
                    // Bot calls and adds raise to pot
                    game.setPot(game.getPot() + raiseAmount);
                } else {
                    // Bot folds — player wins
                    game.setPlayerFolded(false);
                    return endGameFoldByBot(user, game);
                }
            }
            case "FOLD" -> {
                // Player folds — bot wins
                game.setPlayerFolded(true);
                return endGameFold(user, game);
            }
        }

        // Move to next game stage (Flop, Turn, River, Showdown)
        advanceGameStage(game);

        // If showdown reached, evaluate winner
        if (game.getStage() == PokerGameState.GameStage.SHOWDOWN) {
            return evaluateHands(user, game);
        }

        // Return updated game state
        PokerResultDto result = new PokerResultDto();
        result.setGameId(gameId);
        result.setPlayerCards(game.getPlayerCards());
        result.setCommunityCards(getCommunityCardsForStage(game));
        result.setBalance(user.getBalance());
        result.setMessage("Continue: " + game.getStage());
        result.setGameOver(false);
        result.setPot(game.getPot());
        return result;
    }

    /**
     * Compares hands at the showdown stage and determines the winner.
     */
    private PokerResultDto evaluateHands(User user, PokerGameState game) {
        // Combine player and bot hands with community cards
        List<String> board = game.getCommunityCards();
        List<String> playerCombined = new ArrayList<>(game.getPlayerCards());
        playerCombined.addAll(board);

        List<String> botCombined = new ArrayList<>(game.getBotCards());
        botCombined.addAll(board);

        // Evaluate best hands
        PokerHand playerBest = HandEvaluator.evaluateBestHand(playerCombined);
        PokerHand botBest = HandEvaluator.evaluateBestHand(botCombined);

        // Compare hands
        int result = playerBest.compareTo(botBest);
        double winAmount = 0;
        String message;

        if (result > 0) {
            // Player wins
            winAmount = game.getPot();
            user.setBalance(user.getBalance() + winAmount);
            message = "You won (" + playerBest.getDescription() + ")";
        } else if (result < 0) {
            // Bot wins
            message = "You lost (" + botBest.getDescription() + ")";
        } else {
            // Tie
            winAmount = game.getPot() / 2;
            user.setBalance(user.getBalance() + winAmount);
            message = "It's a tie";
        }

        // Save updated balance
        userRepository.save(user);
        game.setAmountWon(winAmount);

        // Prepare result DTO
        PokerResultDto dto = new PokerResultDto();
        dto.setGameId(UUID.randomUUID().toString());
        dto.setPlayerCards(game.getPlayerCards());
        dto.setBotCards(game.getBotCards());
        dto.setCommunityCards(board);
        dto.setPlayerHandRank(playerBest.getDescription());
        dto.setBotHandRank(botBest.getDescription());
        dto.setAmountWon(winAmount);
        dto.setBalance(user.getBalance());
        dto.setGameOver(true);
        dto.setMessage(message);
        dto.setPot(game.getPot());

        return dto;
    }

    /**
     * Ends the game if the player folds.
     */
    private PokerResultDto endGameFold(User user, PokerGameState game) {
        PokerResultDto dto = new PokerResultDto();
        dto.setGameId(UUID.randomUUID().toString());
        dto.setCommunityCards(game.getCommunityCards());
        dto.setBalance(user.getBalance());
        dto.setGameOver(true);
        dto.setMessage("You folded. Bot wins.");
        dto.setPot(game.getPot());
        return dto;
    }

    /**
     * Advances the game to the next stage (FLOP, TURN, RIVER, SHOWDOWN).
     */
    private void advanceGameStage(PokerGameState game) {
        List<String> deck = game.getDeck();
        switch (game.getStage()) {
            case PRE_FLOP -> {
                // Draw and add the flop (3 cards)
                List<String> flop = drawCards(deck, 3);
                game.getCommunityCards().addAll(flop);
                game.setStage(PokerGameState.GameStage.FLOP);
            }
            case FLOP -> {
                // Draw and add the turn (1 card)
                game.getCommunityCards().addAll(drawCards(deck, 1));
                game.setStage(PokerGameState.GameStage.TURN);
            }
            case TURN -> {
                // Draw and add the river (1 card)
                game.getCommunityCards().addAll(drawCards(deck, 1));
                game.setStage(PokerGameState.GameStage.RIVER);
            }
            case RIVER -> {
                // Move to showdown stage
                game.setStage(PokerGameState.GameStage.SHOWDOWN);
            }
        }
    }

    /**
     * Returns the list of visible community cards based on the game stage.
     */
    private List<String> getCommunityCardsForStage(PokerGameState game) {
        return switch (game.getStage()) {
            case PRE_FLOP -> List.of(); // No cards revealed yet
            case FLOP -> game.getCommunityCards().subList(0, 3); // First 3 cards
            case TURN -> game.getCommunityCards().subList(0, 4); // 4 cards
            case RIVER, SHOWDOWN -> game.getCommunityCards(); // All 5 cards
        };
    }

    /**
     * Creates and shuffles a standard 52-card poker deck.
     */
    private List<String> createShuffledDeck() {
        List<String> deck = new ArrayList<>();
        for (String suit : SUITS) {
            for (String rank : RANKS) {
                deck.add(rank + suit); // e.g. "A♠", "10♦"
            }
        }
        Collections.shuffle(deck);
        return deck;
    }

    /**
     * Draws a specified number of cards from the deck.
     */
    private List<String> drawCards(List<String> deck, int count) {
        List<String> hand = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            hand.add(deck.remove(0)); // Remove from top of deck
        }
        return hand;
    }

    /**
     * Determines whether the bot should call a raise based on hand strength and raise amount.
     */
    private boolean botShouldCall(PokerGameState game, double raiseAmount) {
        List<String> board = game.getCommunityCards();

        // Combine bot's cards with community cards
        List<String> botCombined = new ArrayList<>(game.getBotCards());
        botCombined.addAll(board);

        // Evaluate hand strength
        PokerHand botHand = HandEvaluator.evaluateBestHand(botCombined);
        int handStrength = botHand.getStrength(); // 1 = High Card, ..., 9 = Straight Flush

        double callLikelihood;

        // Estimate base likelihood to call depending on hand strength
        switch (handStrength) {
            case 1 -> callLikelihood = 0.1;
            case 2 -> callLikelihood = 0.5;
            case 3 -> callLikelihood = 0.6;
            case 4 -> callLikelihood = 0.7;
            case 5 -> callLikelihood = 0.75;
            case 6 -> callLikelihood = 0.85;
            case 7, 8, 9 -> callLikelihood = 0.95;
            default -> callLikelihood = 0.3;
        }

        // Adjust likelihood based on size of the raise
        double raiseFactor = raiseAmount / game.getPot();
        if (raiseFactor > 1.5) {
            callLikelihood -= 0.2;
        } else if (raiseFactor > 1.0) {
            callLikelihood -= 0.1;
        }

        // Generate random chance
        double chance = Math.random();
        return chance < callLikelihood;
    }

    /**
     * Ends the game if the bot folds to a raise.
     */
    private PokerResultDto endGameFoldByBot(User user, PokerGameState game) {
        double winAmount = game.getPot();
        user.setBalance(user.getBalance() + winAmount);
        userRepository.save(user);

        // Return result to player
        PokerResultDto dto = new PokerResultDto();
        dto.setGameId(UUID.randomUUID().toString());
        dto.setCommunityCards(game.getCommunityCards());
        dto.setBalance(user.getBalance());
        dto.setGameOver(true);
        dto.setMessage("Bot folded. You won the pot: " + winAmount);
        dto.setPot(game.getPot());

        return dto;
    }
}
