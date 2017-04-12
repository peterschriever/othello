package Game.Controllers;

import Framework.AI.BotInterface;
import Framework.GUI.Board;
import Framework.Game.GameLogicInterface;
import Game.Models.Othello;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
public class BoardController extends Board {
    public Othello othello;
    private static final int BOARDSIZE = 8;
    private BotInterface AI;
    private GameLogicInterface gameLogic;

    public BotInterface getAI() {
        return AI;
    }

    public GameLogicInterface getGameLogic() {
        return gameLogic;
    }

    // List of coordinates
    public Map<Integer, int[]> getListOfCoordinates() {
        Map<Integer, int[]> listOfCoordinates = new HashMap<>();
        int key = 0;
        for (int y = 0; y < BOARDSIZE; y++) {
            for (int x = 0; x < BOARDSIZE; x++) {
                listOfCoordinates.put(key, new int[]{x, y});
                key++;
            }
        }
        return listOfCoordinates;
    }
}
