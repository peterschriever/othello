package Game.Controllers;

import Framework.Dialogs.DialogInterface;
import Framework.Dialogs.ErrorDialog;
import Framework.Networking.NetworkEvents;
import Framework.Networking.Response.*;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
public class NetworkEventsController implements NetworkEvents {
    @Override
    public void challengeCancelled(ChallengeCancelledResponse challengeCancelledResponse) {
        System.out.println("challenge cancelled!");
    }

    @Override
    public void challengeReceived(ChallengeReceivedResponse challengeReceivedResponse) {
        System.out.println("challenge received!");
    }

    @Override
    public void gameEnded(GameEndResponse gameEndResponse) {
        System.out.println("gameEnded response!");
    }

    @Override
    public void gameListReceived(GameListResponse gameListResponse) {
        System.out.println("gameList response!");
    }

    @Override
    public void matchReceived(MatchReceivedResponse matchReceivedResponse) {

    }

    @Override
    public void moveReceived(MoveResponse moveResponse) {
        System.out.println("moveReceived response!");
    }

    @Override
    public void ourTurn(OurTurnResponse ourTurnResponse) {
        System.out.println("ourTurn response!");
    }

    @Override
    public void playerListReceived(PlayerListResponse playerListResponse) {
        System.out.println("playerList response!");
    }

    @Override
    public void errorReceived(ErrorResponse errorResponse) {
        DialogInterface errDialog = new ErrorDialog("Network response error", "Likely cause: " + errorResponse.getRequest());
    }
}
