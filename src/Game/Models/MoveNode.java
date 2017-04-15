package Game.Models;

import java.util.List;

/**
 * Created by peterzen on 2017-04-15.
 * Part of the othello project.
 */
public class MoveNode {
    public final Othello.Coords coords;
    public MoveNode[] nextNodes = new MoveNode[32];
    private short nodeCounter;
    private int moveValue;

    public MoveNode(Othello.Coords coords) {
        this.coords = coords;
        this.nodeCounter = 0;
    }

    public void setNextMoves(List<Othello.Coords> moves) {
        for (int i = 0; i < moves.size(); i++) {
            nextNodes[nodeCounter] = new MoveNode(moves.get(i));
            nodeCounter++;
        }
    }

    public void setMoveValue(int value) {
        moveValue = value;
    }

    public int getMoveValue() {
        return moveValue;
    }

}
