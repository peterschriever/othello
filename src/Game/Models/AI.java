package Game.Models;

import Framework.AI.BotInterface;

/**
 * Created by Ruben on 2017-04-14.
 * Part of the othello project.
 */
public class AI implements BotInterface {
    private char[][] board;
    private char maximizingPlayer;

    public AI(char[][] board, char maximizingPlayer) {
        this.board = board;
        this.maximizingPlayer = maximizingPlayer;
    }

    @Override
    public char getPlayer() {
        return maximizingPlayer;
    }

    @Override
    public int[] doTurn(char[][] chars) {
        return new int[0];
    }


}
