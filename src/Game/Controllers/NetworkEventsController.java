package Game.Controllers;

import Framework.Networking.NetworkEvents;
import Framework.Networking.Response.*;
import Game.StartGame;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
public class NetworkEventsController implements NetworkEvents {
    @Override
    public void challengeCancelled(ChallengeCancelledResponse challengeCancelledResponse) {

    }

    @Override
    public void challengeReceived(ChallengeReceivedResponse challengeReceivedResponse) {

    }

    @Override
    public void gameEnded(GameEndResponse gameEndResponse) {

    }

    @Override
    public void gameListReceived(GameListResponse gameListResponse) {

    }

    @Override
    public void matchReceived(MatchReceivedResponse matchReceivedResponse) {
        //Reset the board
        StartGame.getBaseController().getBoardController().loadPreGameBoardState();

        //Disable the controls
        StartGame.getBaseController().getControlsController().disableControls();
        StartGame.getBaseController().getControlsController().disableControls();

        StartGame.getBaseController().getBoardController().ttt = new TTTGame();

    }

    @Override
    public void moveReceived(MoveResponse moveResponse) {

    }

    @Override
    public void ourTurn(OurTurnResponse ourTurnResponse) {

    }

    @Override
    public void playerListReceived(PlayerListResponse playerListResponse) {

    }

    @Override
    public void errorReceived(ErrorResponse errorResponse) {

    }
}
