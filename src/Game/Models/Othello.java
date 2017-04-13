package Game.Models;

import Game.Views.CustomLabel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by Ruben on 10-Apr-17.
 */
public class Othello {

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

        this.board[3][4] =  '2';
        this.board[4][3] =  '2';
    }

    public void showBoard() {
        System.out.println(Arrays.deepToString(this.board));
    }

    public boolean isLegitMove(int y, int x, char player) {
        ArrayList<Integer[]> canSetMove = this.doTurn(y, x, player);
        System.out.println(canSetMove.size());
        this.board[y][x] = 0;

        if(canSetMove.size() > 0) {
            for (Integer[] coords : canSetMove) {
                this.board[coords[0]][coords[1]] = this.switchPlayer(player);
            }

            return true;
        }

        return false;
    }

    /**
     *
     *
     * source: http://stackoverflow.com/questions/20420065/loop-diagonally-through-two-dimensional-array#answer-20422854
     */
    public ArrayList<Integer[]> doTurn(int y, int x, char player) {

        this.board[y][x] = player;

        int[] neighborsX = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] neighborsY = {-1, 0, 1, -1, 1, -1, 0, 1};
        ArrayList<Integer[]> toTurn = new ArrayList<>();

        for (int i = 0; i < neighborsX.length; i++) {

            toTurn.addAll(checkNeighbors(new ArrayList<>(), neighborsY[i], neighborsX[i], y, x, player));
        }

        for(Integer[] coords : toTurn) {
            this.board[coords[0]][coords[1]] = player;
        }

        return toTurn;
    }

    private ArrayList<Integer[]> checkNeighbors(ArrayList<Integer[]> toTurn, int directionY, int directionX, int currentY, int currentX, char player) {

        int newY = currentY + directionY;
        int newX = currentX + directionX;

        if(!this.isInBound(newY, newX)) {
            return new ArrayList<>();
        }

        if(this.board[newY][newX] == 0) {
            return new ArrayList<>();
        }

        if(this.board[newY][newX] == player) {
            return toTurn;
        }
        else {
            toTurn.add(new Integer[]{newY, newX});
            return checkNeighbors(toTurn, directionY, directionX, newY, newX, player);
        }
    }

    private boolean isInBound(int currentY, int currentX) {
        try {
            char c = this.board[currentY][currentX];
        }
        catch (IndexOutOfBoundsException e) {
            return false;
        }

        return true;
    }

    private char switchPlayer(char s) {
        if(s == '1') {
            return '2';
        }
        return '1';
    }


}
