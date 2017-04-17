package Game.Models.GameLogic;

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

    // swaps for Bot:
    private Stack<Coords[]> undoableAITurns;


    public Othello() {
        this.board = new OthelloBoard(BOARDSIZE);
        this.toBeSwapped = new Stack<>();
        this.undoableAITurns = new Stack<>();

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

    public boolean undoAITurn() {
        if (undoableAITurns.empty()) {
            return false;
        }

        Coords[] turn = undoableAITurns.pop();
        for (Coords move : turn) {
            if (move != null) board.set(move.x, move.y, move.old);
        }
        return true;
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

        Coords[] aiTurn = new Coords[32];
        int countAI = 0;

        if (saveSwaps) {
            board.set(x, y, player);
            if (undo) {
                aiTurn[countAI] = new Coords(x, y, player, '\0');
                countAI++;
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
                    this.toBeSwapped.push(flip);

                    // update board with flip
                    board.set(flip, player);
                    if (undo) {
                        aiTurn[countAI] = new Coords(flip.x, flip.y, player, switchPlayer(player));
                        countAI++;
                    }
                }
            }
        }
        if (undo) undoableAITurns.push(aiTurn);
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

    @Override
    public char[][] getBoard() {
        return board.getBoard();
    }

    public boolean gameEndAndWon(char player, char opponent) {
        return isGameOver() && board.getPlayerScore(player) > board.getPlayerScore(opponent);

    }

    private boolean isGameOver() {
        List<Coords> p1Moves = getLegitMoves('1');
        List<Coords> p2Moves = getLegitMoves('2');

        return p1Moves.size() == 0 && p2Moves.size() == 0;
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

        // For Bot undos
        public Coords(int x, int y, char player, char old) {
            this.x = x;
            this.y = y;
            this.player = player;
            this.old = old;
        }
    }
}
