package Game;

import Framework.AI.BotInterface;
import Framework.Config;
import Framework.Dialogs.DialogEvents;
import Framework.Game.GameLogicInterface;
import Framework.GameStart;
import Framework.Networking.Connection;
import Framework.Networking.ConnectionInterface;
import Framework.Networking.NetworkEvents;
import Framework.Networking.SimulatedConnection;
import Game.Controllers.BaseController;
import Game.Controllers.DialogEventsController;
import Game.Controllers.NetworkEventsController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by peterzen on 2017-03-23.
 * Part of the othello project.
 */
public class StartGame extends Application implements GameStart {
    private Stage stage;
    private Scene scene;
    private static ConnectionInterface conn;
    private static ConnectionInterface oldConn;
    private static final NetworkEvents networkEventHandler = new NetworkEventsController();
    private final static DialogEvents dialogEventsController = new DialogEventsController();
    private final static BaseController baseController = new BaseController();


    public static void main(String[] args) {
        System.out.println("hello world");
        launch(args);
    }

    public StartGame(Stage stage, Scene scene) throws IOException {
        // Scene meegegeven die weer wordt vervangen door updateGameScene method. --> dus, is dit nodig?
        this.stage = stage;
        this.scene = scene;

        // setup and save the connection
        String host = Config.get("network", "host");
        int port = Integer.parseInt(Config.get("network", "port"));
//        conn = new Connection(host, port, new NetworkEventController);

        if (!stage.isShowing()) {
            stage.show();
        }

        // update and show the GUI
        updateGameScene();
        this.start();
    }

    public static DialogEvents getDialogEventsController() {
        return dialogEventsController;
    }

    public void updateGameScene() throws IOException {
        // Load view
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Framework/GUI/fxml/View.fxml"));
        fxmlLoader.setController(getBaseController());
        Parent root = fxmlLoader.load();

        Scene gameScene = new Scene(root);
        this.scene = gameScene;
        this.stage.setScene(gameScene);
    }

    public static BaseController getBaseController() {
        return baseController;
    }

    public StartGame() {
        // This constructor only exists to support stand-alone starting
    }

    @Override
    public void start() {
        // when started from either the framework or standalone

    }

    @Override
    public void start(Stage stage) throws Exception {
        // when being started standalone
        new StartGame(stage, null);
    }

    public static ConnectionInterface getConn() {
        return conn;
    }

    public static void toggleConnection() throws IOException {
        ConnectionInterface tempConn;
        if (conn instanceof Connection && oldConn == null) {
            GameLogicInterface gameLogic = getBaseController().getBoardController().getGameLogic();
            BotInterface bot = getBaseController().getBoardController().getAI();
            oldConn = new SimulatedConnection("Tic-tac-toe", gameLogic, bot, networkEventHandler);
        }
        // swaperoo: swap the Simulated and real Connection objects around
        tempConn = conn;
        conn = oldConn;
        oldConn = tempConn;
        System.out.println("now using: " + conn);
        System.out.println("before we used: " + oldConn);
    }
}
