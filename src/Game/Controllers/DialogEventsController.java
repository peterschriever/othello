package Game.Controllers;

import Framework.Dialogs.DialogEvents;
import Framework.Networking.Request.ChallengeAcceptRequest;
import Game.StartGame;

import java.io.IOException;

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
    public void challengeReceived(int challengeNr) {
        ChallengeAcceptRequest acceptRequest = new ChallengeAcceptRequest(StartGame.getConn(), challengeNr);
        try {
            acceptRequest.execute();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setupConnection(String ipAddress, String portNr){
        int portNumber = Integer.parseInt(portNr);

        StartGame.setConnection(ipAddress,portNumber);
    }
}
