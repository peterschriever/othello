package Game.Controllers;

import Framework.Dialogs.*;
import Framework.Networking.NetworkEvents;
import Framework.Networking.Response.*;
import Game.StartGame;
import javafx.application.Platform;
import Game.Models.Othello;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
public class NetworkEventsController implements NetworkEvents {
    @Override
    public void challengeCancelled(ChallengeCancelledResponse response) {
        System.out.println("challenge cancelled!");
    }

    @Override
    public void challengeReceived(ChallengeReceivedResponse response) {
        AbstractDialog challengeDialog = new ChallengeReceivedDialog(StartGame.getDialogEventsController(), response.getChallenger(), response.getChallengeNumber());
        Platform.runLater(challengeDialog::display);
    }

    @Override
    public void gameEnded(GameEndResponse response) {
        // show GameEndedDialog
        String result = response.getResult();
        DialogInterface gameEndedDialog = new MessageDialog(
                "Game has ended!",
                "Game resulted in a " + result + "!",
                "Comment: " + response.getComment() + "\n"
                        + "Player one score: " + response.getPlayerOneScore() + "\n"
                        + "Player two score: " + response.getPlayerTwoScore()
        );
        Platform.runLater(gameEndedDialog::display);

        StartGame.getBaseController().getBoardController().loadPreGameBoardStyle();
        StartGame.getBaseController().getControlsController().enableControls();
    }

    @Override
    public void gameListReceived(GameListResponse response) {
        System.out.println("gameList response!");
    }

    @Override
    public void matchReceived(MatchReceivedResponse response) {
        //Reset the board
        StartGame.getBaseController().getBoardController().loadPreGameBoardState();

        //Disable the controls
        StartGame.getBaseController().getControlsController().disableControls();

        StartGame.getBaseController().getBoardController().othello = new Othello();

        StartGame.getBaseController().getBoardController().startingPlayer = response.getStartingPlayer();
    }

    @Override
    public void moveReceived(MoveResponse response) {
        String player = response.getMovingPlayer();
        if (player.equals(StartGame.getBaseController().getLoggedInPlayer())) {
            return; // ignore moves we have made ourselves.
        }
        if (response.getMoveDetails() != null && response.getMoveDetails().equals("Illegal move")) {
            return; // ignore an illegal move, to prevent getting exceptions on our position conversion
        }

        int position = response.getMovePosition();
        BoardController boardController = StartGame.getBaseController().getBoardController();
        int[] coordinates = boardController.getListOfCoordinates().get(position);

        int x = coordinates[0];
        int y = coordinates[1];

        // update view via BoardController
        boardController.setMove(x, y, player); // @TODO: update gameLogic
    }

    @Override
    public void ourTurn(OurTurnResponse response) {
        // update GUI (and enable possibility to move) to reflect turn change
        StartGame.getBaseController().getBoardController().setOurTurn();

        // let the AI generate a move if needed
        if (StartGame.getBaseController().getControlsController().isBotPlaying()) {
//            StartGame.getBaseController().getBoardController().doAIMove(); @TODO: need AI
        }
    }

    @Override
    public void playerListReceived(PlayerListResponse response) {
        StartGame.getBaseController().getControlsController().updatePlayerList(response.getPlayerList());
    }

    @Override
    public void errorReceived(ErrorResponse response) {
        DialogInterface errDialog = new ErrorDialog("Network response error", "Likely cause: " + response.getRequest());
    }
}
