package Game;

import Framework.Config;
import Framework.GameStart;
import Framework.Networking.Connection;
import javafx.application.Application;
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
    private Connection conn;

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
//        updateGameScene();
//        this.start();
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
}
