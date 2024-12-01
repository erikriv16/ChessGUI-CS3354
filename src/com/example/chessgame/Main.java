package com.example.chessgame;

import ChessGame.Game;

/**
 * The Main class is the entry point of the ChessGame application.
 * It initializes the game and launches the GUI.
 */
public class Main {
    public static void main(String[] args) {
        Game game = new Game();
        game.start(); // Ensure this method makes the GUI visible
    }
}
