package Game.Controllers;

import Framework.Dialogs.DialogEvents;
import Game.StartGame;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
public class DialogEventsController implements DialogEvents {
    @Override
    public void attemptLogin(String playerName) {
        StartGame.getBaseController().attemptPlayerLogin(playerName);
    }

    @Override
    public void challengeReceived(int i) {

    }
}
