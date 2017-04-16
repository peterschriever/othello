package Game.Models;

/**
 * Created by peterzen on 2017-04-15.
 * Part of the othello project.
 */
public class MoveNode {
    public final Othello.Coords coords;
    private final MoveNode parent;
    public MoveNode[] nextNodes = new MoveNode[32];
    private int moveValue;
    private int nodeDepth;

    public MoveNode(Othello.Coords coords, int depth, MoveNode parent) {
        this.coords = coords;
        this.nodeDepth = depth;
        this.parent = parent;
    }

    public void setMoveValue(int value) {
        moveValue = value;
    }

    public int getMoveValue() {
        return moveValue;
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
}
