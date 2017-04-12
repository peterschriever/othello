package Game.Models;

import Framework.AI.BotInterface;
import Framework.Game.GameLogicInterface;

/**
 * Created by Ruben on 10-Apr-17.
 */
public class AI implements BotInterface{
    public AI(GameLogicInterface gameLogic, char playerChar) {

    }

    @Override
    public char getPlayer() {
        return 0;
    }

    @Override
    public int[] doTurn(char[][] chars) {
        return new int[0];
    }
}
