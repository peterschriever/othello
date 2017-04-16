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

    private static final int searchDepth = 2;

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
//        System.out.println("initMoves: ");
//        initMoves.forEach((move) -> System.out.print(move.x + ", " + move.y+"|"));
//        System.out.println();
        Othello.Coords bestMove = evaluator.findBestMove(initMoves, gameLogic);
        System.out.println("BEST MOVE " + bestMove.x + ", " + bestMove.y);

        // undo all board changes
        while (gameLogic.undoAITurn()) {
        }
        while (gameLogic.consumeSwappable() != null) {
        }

        return bestMove;
    }


}
