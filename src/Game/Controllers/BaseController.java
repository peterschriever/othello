package Game.Controllers;

import Framework.Config;
import Framework.Dialogs.ErrorDialog;
import Framework.Dialogs.UserNameDialog;
import Framework.GUI.Base;
import Framework.Networking.Request.LoginRequest;
import Framework.Networking.Request.Request;
import Game.StartGame;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
public class BaseController extends Base {
    private BoardController boardController;
    private ControlsController controlsController;

    private String loggedInPlayer;

    @Override
    public void initialize() {
        super.initialize();
        // setup Connection response observer
        if (StartGame.getConn() != null) {
            StartGame.getConn().setupInputObserver();
        }

        try {
            attemptPlayerLogin(Config.get("game", "playerName"));
        } catch (IOException e) {
            ErrorDialog errorDialog = new ErrorDialog("IOException: could not load from game.properties", "Please contact a project developer.");
            Platform.runLater(errorDialog::display);
        }
    }

    public void attemptPlayerLogin(String playerName) {
        if (StartGame.getConn() == null) {
            // Start a thread that polls until connection is not null, every 100ms
            new Thread(new PollTillConnectionIsUp()).start();
            return;
        }

        Request loginRequest;
        ErrorDialog errorDialog;

        try {
            if (playerName != null && !playerName.trim().equals("")) {
                loginRequest = new LoginRequest(StartGame.getConn(), playerName);
                loginRequest.execute();
                loggedInPlayer = playerName;
                return;
            }
        } catch (IOException | InterruptedException e) {
            errorDialog = new ErrorDialog("IO|InterruptedException: could not send game server Request", "Please contact a project developer.");
            Platform.runLater(errorDialog::display);
        }
        // Config was null or failed: show UsernameDialog
        UserNameDialog loginDialog = new UserNameDialog(StartGame.getDialogEventsController());
        Platform.runLater(loginDialog::display);
    }

    @Override
    protected void loadPartialViews() throws IOException {
        System.out.println("Another init");
        // Load MenuView.fxml
        this.container.getChildren().add(FXMLLoader.load(this.getClass().getResource("/Framework/GUI/fxml/MenuView.fxml")));
        // Load BoardView.fxml
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Framework/GUI/fxml/BoardView.fxml"));

        this.boardController = new BoardController();
        fxmlLoader.setController(this.boardController);
        Parent partial = fxmlLoader.load();
        System.out.println("loading partials from BaseController!");
        container.getChildren().add(partial);

        this.controlsController = new ControlsController();
        fxmlLoader = new FXMLLoader(getClass().getResource("/Game/Views/controls.fxml"));
        fxmlLoader.setController(this.controlsController);
        Parent controls = fxmlLoader.load();
        container.getChildren().add(controls);
    }

    public BoardController getBoardController() {
        return boardController;
    }

    public String getLoggedInPlayer() {
        return loggedInPlayer;
    }

    public ControlsController getControlsController() {
        return this.controlsController;
    }

    private class PollTillConnectionIsUp implements Runnable {
        @Override
        public void run() {
            while (StartGame.getConn() == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            attemptPlayerLogin(null);
        }
    }
}
