package Game.Models;

import Framework.Game.GameLogicInterface;

import java.util.Arrays;
import java.util.Stack;

/**
 * Created by Ruben on 10-Apr-17.
 */
public class Othello implements GameLogicInterface {
    // @TODO idea; use Stack to put all the 'to-be-swapped' stones on, then when updating gui with swaps
    // @TODO (in seperate thread for perf) consume them

    private char[][] board;
    private Stack<Coords> toBeSwapped;

    public Othello() {
        this.board = new char[8][8];
        this.toBeSwapped = new Stack<>();

        this.initFirstState();
    }

    public Coords consumeSwappable() {
        if (toBeSwapped.empty()) {
            return null;
        }

        return toBeSwapped.pop();
    }

    private void initFirstState() {
        //1 is white
        //2 is black
        this.board[3][3] = '1';
        this.board[4][4] = '1';

        this.board[3][4] = '2';
        this.board[4][3] = '2';
    }

    public void showBoard() {
        System.out.println(Arrays.deepToString(this.board));
    }

    // using the private method of doTurn; which is called with saveSwaps=false
    public boolean isLegitMove(int x, int y, char player) {
        boolean result = doTurn(x, y, player, false);
        return result;
    }

    // when calling doTurn from outside this class; do this to force saveSwaps=true
    public boolean doTurn(int x, int y, char player) {
        return doTurn(x, y, player, true);
    }

    /**
     * source: http://stackoverflow.com/questions/20420065/loop-diagonally-through-two-dimensional-array#answer-20422854
     */
    private boolean doTurn(int x, int y, char player, boolean saveSwaps) {
        if (this.board[x][y] != 0) {
            return false;
        }

        if (saveSwaps) {
            this.board[x][y] = player;
        }

        int[] neighborsY = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] neighborsX = {-1, -1, -1, 0, 0, 1, 1, 1};
        boolean result = false;

        for (int i = 0; i < neighborsX.length; i++) {
            Coords[] flips = checkNeighbors(
                    new Coords[16],
                    new int[]{neighborsX[i], neighborsY[i]},
                    new int[]{x, y},
                    player,
                    0
            );

            if (flips != null && flips[0] != null) {
                result = true;

                if (!saveSwaps) {
                    continue; // continue before saving the swaps
                }

                for (Coords flip : flips) {
                    if (flip == null) {
                        continue;
                    }
                    this.toBeSwapped.push(flip);

                    // @TODO: decide whether to adjust internal board here or at consumption of stack
                    this.board[flip.x][flip.y] = player;
                }
            }
        }
        // there were no swaps: false result, otherwise true
        return result;
    }

    private Coords[] checkNeighbors(Coords[] toTurn, int[] direction, int[] current, char player, int count) {
        int newX = current[0] + direction[0];
        int newY = current[1] + direction[1];

        if (!this.isInBound(newX, newY)) {
            return null;
        }

        if (this.board[newX][newY] == 0) {
            return null;
        }

        if (this.board[newX][newY] == player) {
            return toTurn;
        }

        toTurn[count] = new Coords(newX, newY);
        count++;

        return checkNeighbors(toTurn, direction, new int[]{newX, newY}, player, count);

    }

    private boolean isInBound(int currentX, int currentY) {
        try {
            char c = this.board[currentX][currentY];
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        return true;
    }

    private char switchPlayer(char s) {
        if (s == '1') {
            return '2';
        }
        return '1';
    }

    @Override
    public char[][] getBoard() {
        return this.board;
    }

    public class Coords {
        public int x;
        public int y;

        private Coords(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
