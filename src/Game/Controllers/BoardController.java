package Game.Controllers;

import Framework.AI.BotInterface;
import Framework.Config;
import Framework.Dialogs.DialogInterface;
import Framework.Dialogs.ErrorDialog;
import Framework.GUI.Board;
import Framework.Networking.Request.MoveRequest;
import Framework.Networking.Request.Request;
import Game.Models.AI;
import Game.Models.Othello;
import Game.StartGame;
import Game.Views.CustomLabel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
// @TODO: idea; make stack of similar Platform.runLater items and run them in a Task, or single Platform.runLater call
public class BoardController extends Board {
    private BotInterface ai;
    public Othello othello;
    public String startingPlayer;
    private static final int BOARDSIZE = 8;
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
            ai = new AI(othello.getBoard(), Config.get("game", "useCharacterForPlayer").charAt(0));
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
        for (int x = 0; x < BOARDSIZE; x++) {
            for (int y = 0; y < BOARDSIZE; y++) {
                listOfCoordinates.put(key, new int[]{x, y});
                key++;
            }
        }
        return listOfCoordinates;
    }

    public void loadPreGameBoardStyle() {
        Platform.runLater(() -> gridPane.setStyle(preGameGridStyle));
    }

    public void setMove(int x, int y, String player) {
        // Update gameLogic models:
        char playerChar = '1'; // 1: white
        if (player.equals(startingPlayer)) {
            playerChar = '2'; // 2: black
        }

        othello.doTurn(x, y, playerChar); // swaps are placed on Stack

        // Update GUI:
        MoveUpdateTask moveUpdateTask = new MoveUpdateTask(x, y, player);
        new Thread(moveUpdateTask).start();
    }

    private void loadGrid() {
        // y = row, x = column
        for (int x = 0; x < BOARDSIZE; x++) {
            for (int y = 0; y < BOARDSIZE; y++) {
                Image image = new Image(BoardController.class.getClassLoader().getResourceAsStream("Empty.png"));
                Image blackImage = new Image(BoardController.class.getClassLoader().getResourceAsStream("Black.png"));
                Image whiteImage = new Image(BoardController.class.getClassLoader().getResourceAsStream("White.png"));
                ImageView imageView = new ImageView();
                imageView.setFitHeight(cellHeight - 5);
                imageView.setFitWidth(cellWidth - 5);

                if (x == 3 && y == 3 || x == 4 && y == 4) {
                    imageView.setImage(whiteImage);
                } else if (x == 3 && y == 4 || x == 4 && y == 3) {
                    imageView.setImage(blackImage);
                } else {
                    imageView.setImage(image);
                }

                CustomLabel label = new CustomLabel();
                label.setPrefSize(cellWidth, cellHeight);

                label.setX(x);
                label.setY(y);

                label.setOnMouseClicked(this::clickToDoMove);
                gridPane.setHalignment(label, HPos.CENTER);
                label.setStyle(gridCellStyle);
                label.setGraphic(imageView);

                final int finali = x;
                final int finalj = y;
                gridPane.add(label, finali, finalj);
            }
        }

        gridPane.setStyle(preGameGridStyle);
    }

    private CustomLabel makeLabel(int x, int y, String player) {
        CustomLabel newLabel = new CustomLabel();

        ImageView imageView = new ImageView();
        imageView.setFitHeight(30.0);
        imageView.setFitWidth(30.0);

        newLabel.setStyle(cellTakenStyle);

        Image image = new Image(BoardController.class.getClassLoader().getResourceAsStream("White.png"));
        if (player.equals(startingPlayer)) {
            image = new Image(BoardController.class.getClassLoader().getResourceAsStream("Black.png"));
        }
        imageView.setImage(image);


        newLabel.setGraphic(imageView);
        newLabel.setX(x);
        newLabel.setY(y);
        gridPane.setHalignment(newLabel, HPos.CENTER); //@TODO: does this actually do something?
        return newLabel;
    }

    public void loadPreGameBoardState() {
        Platform.runLater(() -> {
            gridPane.getChildren().clear();
            this.loadGrid();
        });
    }

    private void clickToDoMove(MouseEvent mouseEvent) {
        if (!isOurTurn) {
            DialogInterface errDialog = new ErrorDialog("Not your turn!", "Please wait until the borders are green");
            errDialog.display();
            return;
        }

        // Setup variables
        CustomLabel label = (CustomLabel) mouseEvent.getSource();
        String loggedInPlayer = StartGame.getBaseController().getLoggedInPlayer();

        char playerChar = '1';
        if (loggedInPlayer.equals(startingPlayer)) {
            playerChar = '2';
        }

        int lblX = label.getX();
        int lblY = label.getY();

        // Update gameLogic models:
        if (!othello.isLegitMove(lblX, lblY, playerChar)) {
            DialogInterface errDialog = new ErrorDialog("This is not a correct move", "Please choose a legitimate move.");
            errDialog.display();
            return;
        }
        othello.doTurn(lblX, lblY, playerChar);

        // Update GUI:
        MoveUpdateTask moveUpdateTask = new MoveUpdateTask(lblX, lblY, loggedInPlayer);
        new Thread(moveUpdateTask).start();

        // Update GameServer (send Request):
        int pos = lblX * BOARDSIZE + lblY;
        Request moveRequest = new MoveRequest(StartGame.getConn(), pos);
        try {
            moveRequest.execute();

            // Definitively close off our turn:
            Platform.runLater(() -> gridPane.setStyle(theirTurnGridStyle));
            isOurTurn = false;
        } catch (IOException | InterruptedException e) {
            DialogInterface errDialog = new ErrorDialog("Connection error: could not send move", "There was a problem with the server.\nPlease try restarting.");
            errDialog.display();
        }
    }

    public void setOurTurn() {
        isOurTurn = true;
        Platform.runLater(() -> gridPane.setStyle(ourTurnGridStyle));
    }

    /**
     * This class is to make all gui updates corresponding to a moveEvent from either the game, or the network.
     * A prerequisite is that the gameLogic(Othello) model has been updated first.
     */
    private class MoveUpdateTask extends Task<Void> {
        private final int x;
        private final int y;
        private final String playerName;

        public MoveUpdateTask(int x, int y, String playerName) {
            this.x = x;
            this.y = y;
            this.playerName = playerName;
        }

        @Override
        public Void call() {
            // loop through the gridPane nodes and replace the empty spot of the move:
            ObservableList<Node> childrenList = gridPane.getChildren();
            for (Node node : childrenList) {
                if (gridPane.getColumnIndex(node) == x && gridPane.getRowIndex(node) == y
                        && node instanceof CustomLabel) {
                    // swap this node with the newLabel
                    CustomLabel throwaway = makeLabel(x, y, playerName);
                    Platform.runLater(() -> ((CustomLabel) node).setGraphic(throwaway.getGraphic()));
                    break;
                }
            }

            // now start consuming the other stones that need to be swapped from the gameLogic
            // we should consume until we reach the bottom of the stack(null), to avoid more childrenList loops
            ArrayList<Othello.Coords> swappables = new ArrayList<>(7);
            Othello.Coords coords;
            while ((coords = othello.consumeSwappable()) != null) {
                swappables.add(coords);
            }

            // loop through the childrenList once more and update all swappable positions
            for (Node node : childrenList) {
                for (Othello.Coords swappable : swappables) {
                    if (gridPane.getColumnIndex(node) == swappable.x && gridPane.getRowIndex(node) == swappable.y
                            && node instanceof CustomLabel) {
                        // swap this node with the newLabel
                        CustomLabel throwaway = makeLabel(x, y, playerName);
                        Platform.runLater(() -> ((CustomLabel) node).setGraphic(throwaway.getGraphic()));
                    }
                }
            }
            return null;
        }

    }
}
