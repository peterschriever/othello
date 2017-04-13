package Game.Controllers;

import Game.StartGame;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import java.util.List;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
public class ControlsController {
    private boolean isBotPlaying;

    @FXML
    private CheckBox chkPlayAsBot;
    @FXML
    private HBox controlsBox;
    @FXML
    private ListView<String> playerList;

    public void toggleBotPlaying(ActionEvent event) {
        isBotPlaying = chkPlayAsBot.isSelected();
    }

    public boolean isBotPlaying() {
        return isBotPlaying;
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
}
