package com.kasino.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kasino.dto.SpinRequestDto;
import com.kasino.dto.SpinResultDto;
import com.kasino.model.User;
import com.kasino.repository.UserRepository;
/**
 * Service class handling the slot machine game logic,
 * including spinning the reels, calculating winnings,
 * and updating the user's balance.
 */
@Service
public class SlotMachineService {

    // Available symbols displayed on the slot machine reels
    private static final String[] SYMBOLS = {"🍒", "🍋", "🔔", "🍊", "⭐", "💎"};
    
    // Random number generator for spinning reels
    private final Random random = new Random();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminGameStatsService adminGameStatsService;

    /**
     * Spins the slot machine reels for a user with a given bet amount,
     * calculates winnings if any, and updates the user's balance.
     *
     * @param username the player username
     * @param request the spin request containing the bet amount
     * @return the result containing the symbols grid, balance, and message
     */
    public SpinResultDto spin(String username, SpinRequestDto request){
        // Retrieve user or throw if not found
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        double bet = request.getBetAmount();

        SpinResultDto result = new SpinResultDto();

        // Validate bet amount (must be > 0)
        if(bet <= 0){
            result.setMessage("Bet must be greater than 0");
            result.setBalance(user.getBalance());
            return result;
        }

        // Check if user has sufficient funds
        if(user.getBalance() < bet){
            result.setMessage("Insufficient funds");
            result.setBalance(user.getBalance());
            return result;
        }

        // Deduct bet amount from user's balance
        user.setBalance(user.getBalance() - bet);

        // Spin the slot machine: fill 3x3 grid with random symbols
        String[][] grid = new String[3][3];
        for(int row = 0; row < 3; row++){
            for(int col = 0; col < 3; col++){
                grid[row][col] = SYMBOLS[random.nextInt(SYMBOLS.length)];
            }
        }

        // Set the generated symbols grid in the result
        result.setSymbols(grid);

        double winAmount = 0;

        // Check for winning rows (3 matching symbols horizontally)
        for(int row = 0; row < 3; row++){
            if(grid[row][0].equals(grid[row][1]) && grid[row][1].equals(grid[row][2])){
                winAmount += bet * 5;  // Payout: 5x bet per winning row
            }    
        }

        if(winAmount > 0){
            // Player won; update balance and set success message
            result.setMessage("You won! You receive " + winAmount + " 💰");
            user.setBalance(user.getBalance() + winAmount);
        }else{
            // Player lost; encourage to try again
            result.setMessage("Try again!");
        }

        // Set updated balance in result and save user
        result.setBalance(user.getBalance());
        userRepository.save(user);

        //Call Update stats
        double casinoProfit = bet - winAmount;
        adminGameStatsService.updateStatsAfterGame("SlotMachine", winAmount, casinoProfit);

        return result;
    }
}
