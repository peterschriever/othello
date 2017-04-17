package Game.Models;

/**
 * Created by peterzen on 2017-04-15.
 * Part of the othello project.
 */
public class MoveNode {
    public final Othello.Coords coords;
    public MoveNode[] nextNodes = new MoveNode[32];
    public final MoveNode parent;

    public int value;
    public int alpha;
    public int beta;
    public boolean isMaxi;
    public boolean isLeaf = false;
    private int nodeDepth;
    public boolean isVisited;

    public MoveNode(Othello.Coords coords, int depth, MoveNode parent, boolean isMaxi) {
        this.coords = coords;
        this.nodeDepth = depth;
        this.parent = parent;
        this.isMaxi = isMaxi;
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
        if (isMaxi) {
            value = Integer.MIN_VALUE;
        } else {
            value = Integer.MAX_VALUE;
        }
    }

    public MoveNode(Othello.Coords coords, int depth, MoveNode parent, boolean isMaxi, int alpha, int beta) {
        this.coords = coords;
        this.nodeDepth = depth;
        this.parent = parent;
        this.isMaxi = isMaxi;
        this.alpha = alpha;
        this.beta = beta;
        if (isMaxi) {
            value = Integer.MIN_VALUE;
        } else {
            value = Integer.MAX_VALUE;
        }
    }

    public int getDepth() {
        return nodeDepth;
    }

    public void doTurn(Othello gameLogic, char currentPlayer, char otherPlayer, boolean applyParents) {
        if (applyParents && getDepth() > 1) {
            parent.doTurn(gameLogic, otherPlayer, currentPlayer, true);
        }

        gameLogic.doTurn(coords.x, coords.y, currentPlayer, true, true);
    }

    public Othello.Coords[] getMoves() {
        Othello.Coords[] moves = new Othello.Coords[getDepth()];
        moves[getDepth() - 1] = this.coords;
        MoveNode node = this.parent;
        for (int i = getDepth() - 1; i > 1; i--) {
            moves[i] = node.coords;
            node = node.parent;
        }
        return moves;
    }
}
