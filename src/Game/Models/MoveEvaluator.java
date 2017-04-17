package Game.Models;

import java.util.List;
import java.util.Random;

/**
 * Created by peterzen on 2017-04-15.
 * Part of the othello project.
 */

public class MoveEvaluator {
    private final char maxPlayer;
    private final char minPlayer;
    private int searchDepth;
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
        this.searchTree = null;
        this.searchTree = new MoveTree(gameLogic);
        for (int i = 0; i < moves.size(); i++) {
            createMiniMaxTree(moves.get(i), searchTree.getRootNode(), i, gameLogic, false, 1);
        }

        // Moves are loaded into a tree: now traverse and evaluate the best one, starting from the rootNode
        Othello.Coords bestMove = DFS_minimaxValueOf(searchTree.getRootNode(), gameLogic);

        // traverse and find bestMove
//        Othello.Coords bestMove = searchTree.traverseFindBestScoringPath(true);
        if (bestMove != null) {
            return bestMove;
        } else {
            Random r = new Random();
            return moves.get(r.nextInt(moves.size()));
        }
    }

    private Othello.Coords DFS_minimaxValueOf(MoveNode node, Othello gameLogic) {
        int score;
        int bestScore = Integer.MIN_VALUE;
        Othello.Coords bestMove = null;
        for (MoveNode nextNode : node.nextNodes) {
            if (nextNode == null) continue;
            score = DFS_maximumValueOf(nextNode, gameLogic);
            if (score > bestScore) {
                bestMove = nextNode.coords;
                bestScore = score;
            }
        }
        System.out.println("bestMove chosen: "+bestScore);

        return bestMove;
    }

    private int DFS_maximumValueOf(MoveNode node, Othello gameLogic) {
        if (node.isLeaf) {
            evaluateLeafScore(node, gameLogic, minPlayer, maxPlayer);
            return node.value;
        }

        int score;
        int bestScore = Integer.MIN_VALUE;
        for (MoveNode nextNode : node.nextNodes) {
            if (nextNode == null) continue;
            score = DFS_minimumValueOf(nextNode, gameLogic);
            if (score > bestScore) bestScore = score;
        }

        return bestScore;
    }

    private int DFS_minimumValueOf(MoveNode node, Othello gameLogic) {
        if (node.isLeaf) {
            evaluateLeafScore(node, gameLogic, maxPlayer, minPlayer);
            return node.value;
        }

        int score;
        int bestScore = Integer.MAX_VALUE;
        for (MoveNode nextNode : node.nextNodes) {
            if (nextNode == null) continue;
            score = DFS_maximumValueOf(nextNode, gameLogic);
            if (score < bestScore) bestScore = score;
        }

        return bestScore;
    }

    // @TODO: check if traversal is in the right order (DFS vs post order vs other?)
    private void evaluateMiniMaxTree(MoveNode node, int currentDepth, Othello gameLogic) {
        char player = node.isMaxi ? maxPlayer : minPlayer;
        char otherPlayer = !node.isMaxi ? maxPlayer : minPlayer;
        node.isVisited = true;

        if (node.isLeaf) {
//            inheritFieldsFromParent(node); // @TODO: do we need alpha/beta values in leafNodes?

            evaluateLeafScore(node, gameLogic, player, otherPlayer);

            updateParentFields(node);
        } else if (node.getDepth() > 0) { // else if is to skip the rootNode
            // this is not a leaf node, we have to decide if we want to look at other children or cut them off
            inheritFieldsFromParent(node);

            if (node.isMaxi) performBetaCutIfNeeded(node);
            else performAlphaCutIfNeeded(node);
            updateParentFields(node);
        }

        if (currentDepth == searchDepth) return;

        // post order tree traversal
        currentDepth++;
        for (int i = 0; i < node.nextNodes.length; i++) {
            if (node.nextNodes[i] != null && !node.nextNodes[i].isVisited) {
//                System.out.println("currentDepth: " + currentDepth + ", branch:" + i);
                evaluateMiniMaxTree(node.nextNodes[i], currentDepth, gameLogic);
            }
        }
    }

    private void inheritFieldsFromParent(MoveNode node) {
        // @TODO: this might overwrite fields that have been updated by leafNode children
        // only update if the values have not been updated since initial state (to prevent losing child node updates)
        if (node.alpha == Integer.MIN_VALUE) node.alpha = node.parent.alpha;
        if (node.beta == Integer.MAX_VALUE) node.beta = node.parent.beta;
    }

    private void performAlphaCutIfNeeded(MoveNode node) {
        // Mini: if V <= alpha: alpha cut
        if (node.value <= node.alpha) {
            // perform alpha cut; remove all other children (may cut off a sub tree)
            // @TODO: if we set nodes null here, do they still get called in the post order tree traversal loop?
            // @TODO: do we need to deepen till searchDepth and null all children from the param node?
            for (MoveNode nextNode : node.nextNodes) {
                if (nextNode != null && !nextNode.isVisited) {
                    for (MoveNode deeperNode : nextNode.nextNodes) {
                        if (deeperNode != null && !deeperNode.isVisited) deeperNode = null;
                    }
                    nextNode = null;
                }
            }
        }
    }

    private void performBetaCutIfNeeded(MoveNode node) {
        // Maxi: if V >= beta: beta cut
        if (node.value >= node.beta) {
            // perform beta cut; remove all other direct children (no need to check)
            for (MoveNode nextNode : node.nextNodes) {
                if (nextNode != null && !nextNode.isVisited) {
                    // @TODO: if we set nodes null here, do they still get called in the post order tree traversal loop?
                    nextNode = null;
                }
            }
        }
    }

    private void updateParentFields(MoveNode node) {
        if (!node.parent.isMaxi) {
            // parent is Mini
            // if current V is larger, update to lower V
            if (node.parent.value > node.value) node.parent.value = node.value;

            // question: is beta (+inf) >= v? update beta
            if (node.parent.beta >= node.value) {
                node.parent.beta = node.value;
            }
        } else {
            // parent is Maxi
            // if current V is smaller, update to higher V
            if (node.parent.value < node.value) node.parent.value = node.value;

            // question: is alpha (-inf) <= v? update alpha
            if (node.parent.alpha < node.value) node.parent.alpha = node.value;
        }
    }

    private void evaluateLeafScore(MoveNode node, Othello gameLogic, char player, char otherPlayer) {
        if (node.isMaxi) node.doTurn(gameLogic, otherPlayer, player, true);
        else node.doTurn(gameLogic, player, otherPlayer, true);

        int score;
        if (!gameLogic.gameEndAndWon(player, otherPlayer)) { // @TODO: optimize; without check: 7 deep, with 6
            score = simple_evaluateResultingBoard(gameLogic.getOBoard(), player);
            if (player == maxPlayer) score = score * -1;
        } else {
            score = Integer.MAX_VALUE;
        }
//        if (score != 0) System.out.println("calc score: " + score);
        node.value = score;

        // undo all turn changes
        while (gameLogic.undoAITurn()) {
        }
    }

    private void createMiniMaxTree(Othello.Coords move, MoveNode parent,
                                   int branchIndex, Othello gameLogic, boolean isMaxi, int currentDepth) {
        // create the node for this move
        MoveNode node = new MoveNode(move, currentDepth, parent, isMaxi);
        // add the created node to the tree
        parent.nextNodes[branchIndex] = node;

        if (currentDepth == searchDepth) {
            // reached a leaf node!
            node.isLeaf = true;
            return; // do not go beyond searchDepth
        }

        currentDepth++;
        List<Othello.Coords> moves = gameLogic.getLegitMoves(isMaxi ? maxPlayer : minPlayer);
        for (int i = 0; i < moves.size(); i++) {
            createMiniMaxTree(moves.get(i), node, i, gameLogic, !isMaxi, currentDepth);
        }
    }

    private int simple_evaluateResultingBoard(OthelloBoard board, char player) {
        int score = board.getPlayerScore(player);

        for (int[] cornerPosition : cornerPositions) {
            if (board.get(cornerPosition[0], cornerPosition[1]) == player) {
                score += 100;
            }
        }

        for (int[] borderPosition : borderPositions) {
            if (board.get(borderPosition[0], borderPosition[1]) == player) {
                score += 20;
            }
        }

        for (int[] centerPosition : centerPositions) {
            if (board.get(centerPosition[0], centerPosition[1]) == player) {
                score += 15;
            }
        }

        return score;
    }
}
