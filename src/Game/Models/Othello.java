package Game.Models;

import Framework.Game.GameLogicInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Ruben on 2017-04-15.
 * Part of the othello project.
 */
// @TODO: update GameLogicInterface in Framework
public class Othello implements GameLogicInterface, Cloneable {
    private final static int BOARDSIZE = 8;

    private OthelloBoard board;

    // swaps needed for gui:
    private Stack<Coords> toBeSwapped;

    // swaps for AI:
    private Stack<Coords> undoableSwaps;


    public Othello() {
        this.board = new OthelloBoard(BOARDSIZE);
        this.toBeSwapped = new Stack<>();
        this.undoableSwaps = new Stack<>();

        this.initFirstState();
    }

    public List<Coords> getLegitMoves(char player) {
        List<Coords> moves = new ArrayList<>(32);
        // double loop: 8^2 = 64 loops + move check: O(a) where a is amount of empty tiles
        // result: O(n), or maybe O(n log n)
        for (int x = 0; x < board.size(); x++) {
            for (int y = 0; y < board.size(); y++) {
                if (board.get(x, y) == 0) {
                    if (isLegitMove(x, y, player)) {
                        moves.add(new Coords(x, y));
                    }
                }
            }
        }

        return moves;
    }

    public Coords consumeSwappable() {
        if (toBeSwapped.empty()) {
            return null;
        }

        return toBeSwapped.pop();
    }

    public Coords undoAISwap() {
        if (undoableSwaps.empty()) {
            return null;
        }

        Coords move = undoableSwaps.pop();
        board.set(move.x, move.y, move.old);
        return move;
    }

    private void initFirstState() {
        //1 is white
        //2 is black
        board.set(3, 3, '1');
        board.set(4, 4, '1');

        board.set(3, 4, '2');
        board.set(4, 3, '2');
    }

    public void showBoard() {
        System.out.println(board);
    }

    // using the private method of doTurn; which is called with saveSwaps=false
    public boolean isLegitMove(int x, int y, char player) {
        boolean result = doTurn(x, y, player, false, false);
        return result;
    }

    // when calling doTurn from outside this class; do this to force saveSwaps=true
    public boolean doTurn(int x, int y, char player) {
        return doTurn(x, y, player, true, false);
    }

    /**
     * source: http://stackoverflow.com/questions/20420065/loop-diagonally-through-two-dimensional-array#answer-20422854
     */
    public boolean doTurn(int x, int y, char player, boolean saveSwaps, boolean undo) {
        if (board.get(x, y) != 0) {
            return false;
        }

        if (saveSwaps) {
            board.set(x, y, player);
            if (undo) {
                undoableSwaps.push(new Coords(x, y, player, '\0'));
            }
        }

        int[] neighborsY = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] neighborsX = {-1, -1, -1, 0, 0, 1, 1, 1};
        boolean result = false;
        // go through all neighbors: 8 loops, check direction: max 7 recursive calls
        // 8*7 = 56; n = 64; O(n)
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
//                    System.out.println("2 saving flip x:" + flip.x + ", y:" + flip.y);
                    this.toBeSwapped.push(flip);

                    // update board with flip
                    board.set(flip, player);
                    if (undo) {
                        undoableSwaps.push(new Coords(flip.x, flip.y, player, switchPlayer(player)));
                    }
                }
            }
        }
        // there were no swaps: false result, otherwise true
        return result;
    }

    private Coords[] checkNeighbors(Coords[] toTurn, int[] direction, int[] current, char player, int count) {
        int newX = current[0] + direction[0];
        int newY = current[1] + direction[1];

        if (!board.isInBounds(newX, newY)) {
            return null;
        }

        if (board.get(newX, newY) == 0) {
            return null;
        }

        if (board.get(newX, newY) == player) {
            return toTurn;
        }

        toTurn[count] = new Coords(newX, newY);
        count++;

        return checkNeighbors(toTurn, direction, new int[]{newX, newY}, player, count);

    }

    private char switchPlayer(char s) {
        if (s == '1') {
            return '2';
        }
        return '1';
    }

    public OthelloBoard getOBoard() {
        return board;
    }

    @Override
    public char[][] getBoard() {
        return new char[0][];
    }

    public void reset() {
        this.board = new OthelloBoard(BOARDSIZE);
        this.toBeSwapped = new Stack<>();

        this.initFirstState();
    }

    public static class Coords {
        public int x;
        public int y;
        public char player;
        public char old;

        public Coords(int x, int y) {
            this.x = x;
            this.y = y;
        }

        // For AI undos
        public Coords(int x, int y, char player, char old) {
            this.x = x;
            this.y = y;
            this.player = player;
            this.old = old;
        }
    }
}
