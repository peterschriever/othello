package Game.Controllers;

import Framework.AI.BotInterface;
import Framework.Config;
import Framework.Dialogs.DialogInterface;
import Framework.Dialogs.ErrorDialog;
import Framework.GUI.Board;
import Framework.Game.GameLogicInterface;
import Framework.Networking.Request.MoveRequest;
import Framework.Networking.Request.Request;
import Game.Models.AI;
import Game.Models.Othello;
import Game.StartGame;
import Game.Views.CustomLabel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
public class BoardController extends Board {
    private BotInterface ai;
    public Othello othello;
    public String startingPlayer;
    private static final int BOARDSIZE = 8;
    private BotInterface AI;
    private static double cellWidth;
    private static double cellHeight;

    private static final String gridCellStyle = "-fx-border-color: black; -fx-border-width:1;";
    private static final String cellTakenStyle = "-fx-border-color: red; -fx-border-width:1;";
    private static final String preGameGridStyle = "-fx-border-color: yellow;-fx-border-width:3;-fx-padding: 10 10 10 10;-fx-border-insets: 10 10 10 10;";
    private static final String ourTurnGridStyle = "-fx-border-color: green;-fx-border-width:3;-fx-padding: 10 10 10 10;-fx-border-insets: 10 10 10 10;";
    private static final String theirTurnGridStyle = "-fx-border-color: red;-fx-border-width:3;-fx-padding: 10 10 10 10;-fx-border-insets: 10 10 10 10;";

    private boolean isOurTurn = false;

    public void initialize() {
        othello = new Othello();
        try {
            ai = new AI(othello, Config.get("game", "useCharacterForOpponent").charAt(0));
        } catch (IOException e) {
            DialogInterface errDialog = new ErrorDialog("Config error", "Could not load property: useCharacterForPlayer." +
                    "\nPlease check your game.properties file.");
            errDialog.display();
        }

        cellWidth = (gridPane.getPrefWidth() / BOARDSIZE) - 2;
        cellHeight = (gridPane.getPrefWidth() / BOARDSIZE) - 2;
        drawGrid(BOARDSIZE);
        loadGrid();

        othello.showBoard();
    }

    public BotInterface getAI() {
        return ai;
    }

    public Othello getGameLogic() {
        return othello;
    }

    // List of coordinates
    public Map<Integer, int[]> getListOfCoordinates() {
        Map<Integer, int[]> listOfCoordinates = new HashMap<>();
        int key = 0;
        for (int y = 0; y < BOARDSIZE; y++) {
            for (int x = 0; x < BOARDSIZE; x++) {
                listOfCoordinates.put(key, new int[]{x, y});
                key++;
            }
        }
        return listOfCoordinates;
    }

    public void loadPreGameBoardStyle() {
        Platform.runLater(() -> gridPane.setStyle(preGameGridStyle));
    }

    public synchronized void setMove(int x, int y, String player) {
        // model updaten
        char playerChar = '1';
        if (player.equals(startingPlayer)) {
            playerChar = '2';
        }
        ArrayList<Integer[]> toSwap = othello.doTurn(y, x, playerChar); // @TODO maybe switch x and y

        CustomLabel newLabel = makeLabel(y, x, player);
        ObservableList<Node> childrenList = gridPane.getChildren();
        for (Node node : childrenList) {
            if (gridPane.getRowIndex(node) == y && gridPane.getColumnIndex(node) == x) {
                Platform.runLater(() -> gridPane.getChildren().remove(node));
                break;
            }
        }
        // gridPane updaten with move
        Platform.runLater(() -> gridPane.add(newLabel, x, y));

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Integer[] coords : toSwap) {
            this.swap(coords, player);
        }
    }

    private void loadGrid() {
        // y = row, x = column
        for (int y = 0; y < BOARDSIZE; y++) {
            for (int x = 0; x < BOARDSIZE; x++) {
                Image image = new Image(BoardController.class.getClassLoader().getResourceAsStream("Empty.png"));
                Image blackImage = new Image(BoardController.class.getClassLoader().getResourceAsStream("Black.png"));
                Image whiteImage = new Image(BoardController.class.getClassLoader().getResourceAsStream("White.png"));
                ImageView imageView = new ImageView();
                imageView.setFitHeight(cellHeight - 5);
                imageView.setFitWidth(cellWidth - 5);
                if (y == 3 && x == 3 || y == 4 && x == 4) {
                    imageView.setImage(whiteImage);
                } else if (y == 3 && x == 4 || y == 4 && x == 3) {
                    imageView.setImage(blackImage);
                } else {
                    imageView.setImage(image);
                }
                CustomLabel label = new CustomLabel();
                label.setPrefSize(cellWidth, cellHeight);
                label.setY(x);
                label.setX(y);
                label.setOnMouseClicked(this::clickToDoMove);
                gridPane.setHalignment(label, HPos.CENTER);
                label.setStyle(gridCellStyle);
                label.setGraphic(imageView);

                final int finali = x;
                final int finalj = y;
                Platform.runLater(() -> gridPane.add(label, finali, finalj));
            }
        }
        // @TODO beginopstelling doorgeven aan model?
        // of wordt die opstelling al meteen gezet wann het model (Othello / GameLogic) wordt aangemaakt
        gridPane.setStyle(preGameGridStyle);
    }

    private synchronized CustomLabel makeLabel(int x, int y, String player) {
        CustomLabel newLabel = new CustomLabel();
        ImageView imageView = new ImageView();
        imageView.setFitHeight(30.0);
        imageView.setFitWidth(30.0);
        newLabel.setStyle(cellTakenStyle);
        if (player.equals(startingPlayer)) {
            Image image = new Image(BoardController.class.getClassLoader().getResourceAsStream("Black.png"));
            imageView.setImage(image);
        } else {
            Image image = new Image(BoardController.class.getClassLoader().getResourceAsStream("White.png"));
            imageView.setImage(image);
        }
        newLabel.setGraphic(imageView);
        newLabel.setX(x);
        newLabel.setY(y);
        gridPane.setHalignment(newLabel, HPos.CENTER);
        return newLabel;
    }

    public void loadPreGameBoardState() {
        Platform.runLater(() -> gridPane.getChildren().clear());
        Platform.runLater(this::loadGrid);
    }

    private void clickToDoMove(MouseEvent mouseEvent) {
        CustomLabel label = (CustomLabel) mouseEvent.getSource();

        char playerChar = '1';
        if (StartGame.getBaseController().getLoggedInPlayer().equals(startingPlayer)) {
            playerChar = '2';
        }

        if (this.othello.isLegitMove(label.getY(), label.getX(), playerChar)) {
            System.out.println("Uep");
            //replace the old label with a stone
            CustomLabel newLabel = this.makeLabel(label.getX(), label.getY(), StartGame.getBaseController().getLoggedInPlayer());
            gridPane.getChildren().remove(label);
            gridPane.add(newLabel, label.getY(), label.getX());

            //update othello
            ArrayList<Integer[]> toSwap = othello.doTurn(label.getY(), label.getX(), playerChar);
            for (Integer[] coords : toSwap) {
                this.swap(coords, StartGame.getBaseController().getLoggedInPlayer());
            }
            // send moveRequest to the server
            int pos = label.getY() * BOARDSIZE + label.getX();
            Request moveRequest = new MoveRequest(StartGame.getConn(), pos);
            try {
                moveRequest.execute();
                isOurTurn = false;
                Platform.runLater(() -> gridPane.setStyle(theirTurnGridStyle));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            //Show notification that the move is incorrect
        }
    }

    private synchronized void swap(Integer[] coords, String player) {
        ObservableList<Node> childrenList = gridPane.getChildren();

        for (Node label : childrenList) {
            if (gridPane.getRowIndex(label).equals(coords[1]) && gridPane.getColumnIndex(label).equals(coords[0])) {
                Platform.runLater(() -> gridPane.getChildren().remove(label));
            }
            Platform.runLater(() -> gridPane.add(makeLabel(coords[1], coords[0], player), coords[0], coords[1]));
        }
    }

    public void setOurTurn() {
        isOurTurn = true;
        Platform.runLater(() -> gridPane.setStyle(ourTurnGridStyle));
    }
}
