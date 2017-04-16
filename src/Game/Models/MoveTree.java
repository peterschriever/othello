package Game.Models;

/**
 * Created by peterzen on 2017-04-15.
 * Part of the othello project.
 */
public class MoveTree {
    private final MoveNode rootNode = new MoveNode(null, 0, null);
    private Othello gameLogic;

    public MoveTree(Othello gameLogic) {
        this.gameLogic = gameLogic;
    }

    public MoveNode getRootNode() {
        return rootNode;
    }

    public Othello.Coords traverseFindBestScoringPath(boolean isMaxing) {
        int pathScore = 0;
        int score;
        Othello.Coords bestMove = null;
        for (MoveNode nextNode : getRootNode().nextNodes) {
            if (nextNode == null) continue;

            score = traverseAndFindScore(nextNode, isMaxing);
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
        int pathScore = node.getMoveValue(); // the minimum value of this path, when we don't consider children
        int score;
        for (MoveNode nextNode : node.nextNodes) {
            if (nextNode == null) {
                continue;
            }

            score = traverseAndFindScore(nextNode, !isMaxing) + node.getMoveValue();

            // maxi
            if (isMaxing && (score > pathScore)) pathScore = score;
            // mini
            if (!isMaxing && (score < pathScore)) pathScore = score;
        }

        return pathScore;
    }


    public Othello getGameLogicClone() {
        return gameLogic;
    }
}
