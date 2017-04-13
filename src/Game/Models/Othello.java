package Game.Models;

import Framework.Game.GameLogicInterface;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ruben on 10-Apr-17.
 */
public class Othello implements GameLogicInterface {

    private char[][] board;

    public Othello() {
        this.board = new char[8][8];

        this.initFirstState();
    }

    private void initFirstState() {
        //1 is white
        //2 is black
        this.board[3][3] = '1';
        this.board[4][4] = '1';

        this.board[3][4] = '2';
        this.board[4][3] = '2';

        this.showBoard();
    }

    public void showBoard() {
        System.out.println(Arrays.deepToString(this.board));
    }

    public boolean isLegitMove(int x, int y, char player) {
        ArrayList<Integer[]> canSetMove = this.doTurn(x, y, player);
        this.board[x][y] = 0;

        if (canSetMove.size() > 0) {
            for (Integer[] coords : canSetMove) {
                this.board[coords[0]][coords[1]] = this.switchPlayer(player);
            }

            return true;
        }

        return false;
    }

    /**
     * source: http://stackoverflow.com/questions/20420065/loop-diagonally-through-two-dimensional-array#answer-20422854
     */
    public ArrayList<Integer[]> doTurn(int x, int y, char player) {

        this.board[x][y] = player;

        int[] neighborsY = {-1, 0, 1, -1, 1, -1, 0, 1};
        int[] neighborsX = {-1, -1, -1, 0, 0, 1, 1, 1};
        ArrayList<Integer[]> toTurn = new ArrayList<>();

        for (int i = 0; i < neighborsX.length; i++) {
            ArrayList<Integer[]> flips = checkNeighbors(new ArrayList<>(), neighborsX[i], neighborsY[i], x, y, player);
            if (flips != null && flips.size() > 0) {
//                for (Integer[] flip : flips) {
//                    System.out.println("flip found: 0:" + flip[0] + ", 1:" + flip[1]);
//                }
                toTurn.addAll(flips);
            }
        }

        for (Integer[] coords : toTurn) {
            this.board[coords[0]][coords[1]] = player;
        }

        return toTurn;
    }

    private ArrayList<Integer[]> checkNeighbors(ArrayList<Integer[]> toTurn, int directionX, int directionY, int currentX, int currentY, char player) {

        int newX = currentX + directionX;
        int newY = currentY + directionY;

        if (!this.isInBound(newX, newY)) {
//            System.out.println("1 not in bounds");
            return null;
        }

        if (this.board[newX][newY] == 0) {
//            System.out.println("2 empty tile x: " + newX + ", y: " + newY);
            return null;
        }

        if (this.board[newX][newY] == player) {
//            System.out.println("3 player tile found");
            return toTurn;
        }

        toTurn.add(new Integer[]{newX, newY});
//        System.out.println("0 flippable added, continue down the rabbit hole x: " + newX + ", y: " + newY);
        return checkNeighbors(toTurn, directionX, directionY, newX, newY, player);

    }

    private boolean isInBound(int currentX, int currentY) {
        try {
            char c = this.board[currentY][currentX];
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
}
