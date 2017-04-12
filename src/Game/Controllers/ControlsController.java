package Game.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
public class ControlsController {

    private boolean isBotPlaying;

    @FXML
    private CheckBox chkPlayAsBot;

    public void toggleBotPlaying(ActionEvent event) {
        isBotPlaying = chkPlayAsBot.isSelected();
    }

    public boolean isBotPlaying() {
        return isBotPlaying;
    }

}
