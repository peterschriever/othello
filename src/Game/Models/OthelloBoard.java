package Game.Models;

import java.util.Arrays;

/**
 * Created by peterzen on 2017-04-15.
 * Part of the othello project.
 */
public class OthelloBoard implements Cloneable {
    private char[][] board;

    public OthelloBoard(int boardsize) {
        this.board = new char[boardsize][boardsize];
    }

    // For cloning purposes only!
    public OthelloBoard(OthelloBoard original) {
        this.board = original.board;
    }

    public int size() {
        return board.length;
    }

    public char get(int x, int y) {
        return board[x][y];
    }

    public char get(Othello.Coords coords) {
        return get(coords.x, coords.y);
    }

    public void set(int x, int y, char val) {
        board[x][y] = val;
    }

    public void set(Othello.Coords coords, char val) {
        set(coords.x, coords.y, val);
    }

    public boolean isInBounds(int x, int y) {
        try {
            char c = this.board[x][y];
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(board);
    }

    public char[][] getBoard() {
        return board;
    }

    public OthelloBoard getClone() {
        try {
            return ((OthelloBoard) this.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getPlayerScore(char player) {
        int i = 0;
        for (char[] chars : board) {
            for (char tile : chars) {
                if (tile == player) i++;
            }
        }
        return i;
    }
}
