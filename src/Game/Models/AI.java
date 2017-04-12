package Game.Models;

/**
 * Created by Ruben on 10-Apr-17.
 */
public class AI {

    private Othello othello;

    private String[][] board;

    public AI(Othello othello, String[][] board) {
        this.othello = othello;
        this.board = board;
    }
}
