package Game.Controllers;

import Framework.Config;
import Framework.Dialogs.DialogInterface;
import Framework.Dialogs.ErrorDialog;
import Framework.Networking.Request.ChallengeRequest;
import Framework.Networking.Request.GetPlayerListRequest;
import Framework.Networking.Request.Request;
import Game.StartGame;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
public class ControlsController implements Initializable {
    private boolean isBotPlaying;

    @FXML
    private Button challengePlayer;
    @FXML
    private Button challengeComputer;
    @FXML
    private CheckBox chkPlayAsBot;
    @FXML
    private HBox controlsBox;
    @FXML
    private ListView<String> playerList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //setting the player list
        this.initPlayerChallenging();
        this.initComputerChallenging();

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new PlayerGetter(), 0, 5, TimeUnit.SECONDS);
    }

    public void toggleBotPlaying(ActionEvent event) {
        isBotPlaying = chkPlayAsBot.isSelected();
    }

    public boolean isBotPlaying() {
        return isBotPlaying;
    }

    private void initComputerChallenging() {
        challengeComputer.setOnAction(e -> this.challengeComputer());
    }

    private void initPlayerChallenging() {
        ObservableList<String> possiblePlayers = FXCollections.observableArrayList(
                "Ruben", "Peter", "Eran", "Femke");
        playerList.setItems(possiblePlayers);

        challengePlayer.setOnAction(e -> this.challengePlayer());
    }

    @FXML
    private void challengePlayer() {
        String selectedPlayer = playerList.getSelectionModel().getSelectedItem();

        if (selectedPlayer == null) {
            //no player selected
            new ErrorDialog("Error", "Please select a user").display();
        } else {
            try {
                ChallengeRequest request = new ChallengeRequest(StartGame.getConn(), selectedPlayer, "Reversi");
                request.execute();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void challengeComputer() {
        try {
            StartGame.toggleConnection();
            Request challengeBot = new ChallengeRequest(StartGame.getConn(), Config.get("game", "botName"), "Reversi");
            challengeBot.execute();
        } catch (InterruptedException | IOException e) {
            DialogInterface errorDialog = new ErrorDialog("Config error", "Could not load property: useCharacterForPlayer." +
                    "\nPlease check your game.properties file.");
            Platform.runLater(errorDialog::display);
        }
    }

    /**
     * Disable or enable all the controls
     */
    public void disableControls() {
        if (!controlsBox.isDisable()) {
            controlsBox.setDisable(true);
        }
    }

    public void enableControls() {
        if (controlsBox.isDisable()) {
            controlsBox.setDisable(false);
        }
    }

    public void updatePlayerList(List<String> playerList) {
        ObservableList<String> list = FXCollections.observableArrayList(playerList);
        list.remove(StartGame.getBaseController().getLoggedInPlayer()); // make sure not to include ourselves
        Platform.runLater(() -> this.playerList.setItems(list));
    }

    private class PlayerGetter implements Runnable {
        @Override
        public void run() {
            try {
                Request request = new GetPlayerListRequest(StartGame.getConn());
                request.execute();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
