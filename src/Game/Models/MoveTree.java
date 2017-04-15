package Game.Models;

/**
 * Created by peterzen on 2017-04-15.
 * Part of the othello project.
 */
public class MoveTree {
    private final MoveNode rootNode = new MoveNode(null);
    private Othello gameLogic;

    public MoveTree(Othello gameLogic) {
        this.gameLogic = gameLogic;
    }

    public MoveNode getRootNode() {
        return rootNode;
    }

    public Othello.Coords traverseFindBestScoringPath(boolean isMaxing) {
        int pathScore = 0;
        Othello.Coords bestMove = null;
        for (MoveNode nextNode : getRootNode().nextNodes) {
            if (nextNode == null) continue;

            int score = traverseAndFindScore(nextNode, !isMaxing);
            if (score > pathScore) {
                pathScore = score;
                bestMove = nextNode.coords;
            }
        }

        if (bestMove != null) {
            return bestMove;
        }
        return null;
    }

    private int traverseAndFindScore(MoveNode node, boolean isMaxing) {
        int pathScore = 0;
        for (MoveNode nextNode : node.nextNodes) {
            if (nextNode == null) continue;

            int score = traverseAndFindScore(nextNode, !isMaxing) + node.getMoveValue();
            if (isMaxing && score > pathScore) pathScore = score;
            if (isMaxing && score < pathScore) pathScore = score;
        }
        return pathScore;
    }


    public Othello getGameLogicClone() {
        return gameLogic;
    }
}
