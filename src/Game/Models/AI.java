package Game.Models;

import Framework.AI.BotInterface;

import java.util.List;

/**
 * Created by Ruben on 2017-04-14.
 * Part of the othello project.
 */
// @TODO: update BotInterface in Framework
public class AI implements BotInterface {
    private char maximizingPlayer;
    private MoveEvaluator evaluator;

    private static final int searchDepth = 5;

    public AI(char maximizingPlayer, char minimizingPlayer) {
        this.maximizingPlayer = maximizingPlayer;
        this.evaluator = new MoveEvaluator(searchDepth, maximizingPlayer, minimizingPlayer);
    }

    @Override
    public char getPlayer() {
        return maximizingPlayer;
    }

    @Override
    public int[] doTurn(char[][] chars) {
        return new int[0];
    }

    public Othello.Coords doTurn(Othello gameLogic) {
        List<Othello.Coords> initMoves = gameLogic.getLegitMoves(maximizingPlayer);
        if (initMoves.size() == 0) {
            System.out.println("WHY IS THIS 0? NO MOVES FOUND");
        }

        // start recursive search for best move:
        Othello.Coords bestMove = evaluator.findBestMove(initMoves, gameLogic);

        // undo all board changes
        while (gameLogic.undoAITurn()) {
        }
        while (gameLogic.consumeSwappable() != null) {
        }

        return bestMove;
    }


}
