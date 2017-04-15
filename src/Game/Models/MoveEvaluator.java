package Game.Models;

import java.util.List;
import java.util.Random;

/**
 * Created by peterzen on 2017-04-15.
 * Part of the othello project.
 */
// @TODO: save a copy of OthelloBoard with the searchTree, apply the moves(MoveNodes) on this board,
// @TODO: and evaluate the moveNode values based on the possible swaps and possible stone positions
// @TODO: do not forget to apply the moves on the original Board when traversing the tree
// applying maximisation at the odd levels, and minimisation at the even levels
// stop creating when searchDepth is reached, and traverse to the best move
public class MoveEvaluator {
    private final char maxPlayer;
    private final char minPlayer;
    private int searchDepth;
    private int currentDepth = 1;
    private MoveTree searchTree;

    private final static int[][] centerPositions = new int[][]{{3, 3}, {3, 4}, {4, 3}, {4, 4}};
    private final static int[][] cornerPositions = new int[][]{{0, 0}, {7, 0}, {0, 7}, {7, 7}};
    private final static int[][] borderPositions = new int[][]{
            {2, 0}, {3, 0}, {4, 0}, {5, 0},
            {0, 2}, {0, 3}, {0, 4}, {0, 5},
            {2, 7}, {3, 7}, {4, 7}, {5, 7},
            {7, 2}, {7, 3}, {7, 4}, {7, 5}
    };


    public MoveEvaluator(int searchDepth, char maximizingPlayer, char minimizingPlayer) {
        this.searchDepth = searchDepth;
        this.maxPlayer = maximizingPlayer;
        this.minPlayer = minimizingPlayer;
    }


    public Othello.Coords findBestMove(List<Othello.Coords> moves, Othello gameLogic) {
        // create a new tree
        this.searchTree = new MoveTree(gameLogic);

        for (int i = 0; i < moves.size(); i++) {
            createMiniMaxTree(moves.get(i), searchTree.getRootNode(), i, searchTree.getGameLogicClone(), true);
        }

        Othello.Coords bestMove = searchTree.traverseFindBestScoringPath(true);
        if (bestMove != null) {
            return bestMove;
        } else {
            Random r = new Random();
            System.out.println("RAND: bound positive?: " + moves.size());
            return moves.get(r.nextInt(moves.size()));
        }
    }

    private void createMiniMaxTree(Othello.Coords move, MoveNode node, int branchIndex, Othello gameLogic, boolean isMaxing) {
        char player = isMaxing ? maxPlayer : minPlayer;

        // apply move
        gameLogic.doTurn(move.x, move.y, player, true, true);

        // create the resulting MoveNode
        MoveNode resultNode = new MoveNode(move);

        // evaluate the resulting board state for the current player & save this with the resultNode
        int score = simple_evaluateResultingBoard(gameLogic, move, isMaxing);
        resultNode.setMoveValue(score);

        // add this resultNode to the currentNode branches
        node.nextNodes[branchIndex] = resultNode;

        // increment depth
        currentDepth++;
        if (currentDepth >= searchDepth) {
            return;
        }

        // with the new move applied to the board and depth increased, find all new moves and do a recursive call
        isMaxing = !isMaxing; // toggle miniMax
        List<Othello.Coords> moves = gameLogic.getLegitMoves(isMaxing ? maxPlayer : minPlayer);
        for (int i = 0; i < moves.size(); i++) {
            createMiniMaxTree(moves.get(i), resultNode, i, gameLogic, !isMaxing);
        }
    }

    private int simple_evaluateResultingBoard(Othello gameLogic, Othello.Coords move, boolean isMaxing) {
        int score = 0;
        int centerSwaps = 0;
        Othello.Coords swap;
        while ((swap = gameLogic.consumeSwappable()) != null) {
            score++; // 1pt per swap
            if (isCenterSwap(swap)) {
                centerSwaps++;
            }
        }

        if (moveIsInCorner(move)) {
            score += 50;
        }

        if (moveIsInBorders(move)) {
            score += 20;
        }

        score += centerSwaps * 15;

        return isMaxing ? score : score * -1;
    }

    private boolean moveIsInBorders(Othello.Coords move) {
        for (int[] borderPosition : borderPositions) {
            if (borderPosition[0] == move.x && borderPosition[1] == move.y) {
                return true;
            }
        }
        return false;
    }

    private boolean moveIsInCorner(Othello.Coords move) {
        for (int[] cornerPosition : cornerPositions) {
            if (cornerPosition[0] == move.x && cornerPosition[1] == move.y) {
                return true;
            }
        }
        return false;
    }

    private boolean isCenterSwap(Othello.Coords swap) {
        for (int[] centerPosition : centerPositions) {
            if (centerPosition[0] == swap.x && centerPosition[1] == swap.y) {
                return true;
            }
        }
        return false;
    }
}
