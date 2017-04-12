package Game.Controllers;

import Framework.Dialogs.ConnectionDialog;
import Framework.Dialogs.DialogEvents;
import Framework.Dialogs.DialogInterface;
import Framework.GameStart;
import Game.StartGame;

import java.io.IOException;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
public class DialogEventsController implements DialogEvents {
    @Override
    public void attemptLogin(String s) {

    }

    @Override
    public void challengeReceived(int i) {

    }

    @Override
    public void setupConnection(String ipAddress, String portNr){
        int portNumber = Integer.parseInt(portNr);

        try {
            StartGame.setConnection(ipAddress,portNumber);
            StartGame.getConn().setupInputObserver();

        } catch (Exception e) {
            e.printStackTrace();

            DialogInterface networkDialog = new ConnectionDialog(StartGame.getDialogEventsController());
            networkDialog.display();
        }
    }
}
