package Game.Controllers;

import Framework.AI.BotInterface;
import Framework.Config;
import Framework.Dialogs.DialogInterface;
import Framework.Dialogs.ErrorDialog;
import Framework.GUI.Board;
import Framework.Game.GameLogicInterface;
import Game.Models.AI;
import Game.Models.Othello;
import Game.Views.CustomLabel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

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
    private static final int BOARDSIZE = 8;
    private BotInterface AI;
    private GameLogicInterface gameLogic;
    private static double cellWidth;
    private static double cellHeight;

    private static final String gridCellStyle = "-fx-border-color: black; -fx-border-width:1;";
    private static final String cellTakenStyle = "-fx-border-color: red; -fx-border-width:1;";
    private static final String preGameGridStyle = "-fx-border-color: yellow;-fx-border-width:3;-fx-padding: 10 10 10 10;-fx-border-insets: 10 10 10 10;";
    private static final String ourTurnGridStyle = "-fx-border-color: green;-fx-border-width:3;-fx-padding: 10 10 10 10;-fx-border-insets: 10 10 10 10;";
    private static final String theirTurnGridStyle = "-fx-border-color: red;-fx-border-width:3;-fx-padding: 10 10 10 10;-fx-border-insets: 10 10 10 10;";

    private boolean isOurTurn = false;

    public void initialize() {
        gameLogic = new Othello();
        try {
            ai = new AI(gameLogic, Config.get("game", "useCharacterForOpponent").charAt(0));
        } catch (IOException e) {
            DialogInterface errDialog = new ErrorDialog("Config error", "Could not load property: useCharacterForPlayer." +
                    "\nPlease check your game.properties file.");
            errDialog.display();
        }

        cellWidth = (gridPane.getPrefWidth() / BOARDSIZE) - 2;
        cellHeight = (gridPane.getPrefWidth() / BOARDSIZE) - 2;
        drawGrid(BOARDSIZE);
        loadGrid();
    }

    public BotInterface getAI() {
        return ai;
    }

    public GameLogicInterface getGameLogic() {
        return gameLogic;
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

    public void setMove(int x, int y, String player) {
        CustomLabel newLabel = makeLabel(x, y, player);
        ObservableList<Node> childrenList = gridPane.getChildren();
        for (Node node : childrenList) {
            if (gridPane.getRowIndex(node) == y && gridPane.getColumnIndex(node) == x) {
                Platform.runLater(() -> gridPane.getChildren().remove(node));
                break;
            }
        }
        // gridPane updaten with move
        Platform.runLater(() -> gridPane.add(newLabel, x, y));

        // model updaten
        char turn = player.charAt(0);
//        gameLogic.doTurn(y, x, turn); @TODO
    }

    private CustomLabel makeLabel(int x, int y, String turn) {
        CustomLabel newLabel = new CustomLabel();
        ImageView imageView = new ImageView();
        imageView.setFitHeight(50.0);
        imageView.setFitWidth(50.0);
        newLabel.setStyle(cellTakenStyle);
        if (turn.equals("X")) {
            Image image = new Image(BoardController.class.getClassLoader().getResourceAsStream("Black.png"));
            imageView.setImage(image);
            newLabel.setGraphic(imageView);
            newLabel.setX(x);
            newLabel.setY(y);
            gridPane.setHalignment(newLabel, HPos.CENTER);
        } else {
            Image image = new Image(BoardController.class.getClassLoader().getResourceAsStream("White.png"));
            imageView.setImage(image);
            newLabel.setGraphic(imageView);
            newLabel.setX(x);
            newLabel.setY(y);
            gridPane.setHalignment(newLabel, HPos.CENTER);
        }
        return newLabel;

    }

    private void loadGrid() {
        int i;
        int j;
        // i = row, j = column
        for (i = 0; i < BOARDSIZE; i++) {
            for (j = 0; j < BOARDSIZE; j++) {
//                System.out.println("I: " + i + " J: " + j);
                Image image = new Image(BoardController.class.getClassLoader().getResourceAsStream("Empty.png"));
                Image blackImage = new Image(BoardController.class.getClassLoader().getResourceAsStream("Black.png"));
                Image whiteImage = new Image(BoardController.class.getClassLoader().getResourceAsStream("White.png"));
                ImageView imageView = new ImageView();
                imageView.setFitHeight(cellHeight - 5);
                imageView.setFitWidth(cellWidth - 5);
                if (i == 3 && j == 3 || i == 4 && j == 4) {
                    imageView.setImage(whiteImage);
                } else if (i == 3 && j == 4 || i == 4 && j == 3) {
                    imageView.setImage(blackImage);
                } else {
                    imageView.setImage(image);
                }
                CustomLabel label = new CustomLabel();
                label.setPrefSize(cellWidth, cellHeight);
                label.setX(i);
                label.setY(j);
                label.setOnMouseClicked(this::clickToDoMove);
                gridPane.setHalignment(label, HPos.CENTER);
                label.setStyle(gridCellStyle);
                label.setGraphic(imageView);

                final int finali = i;
                final int finalj = j;
                Platform.runLater(() -> gridPane.add(label, finalj, finali));
            }
        }
        // @TODO beginopstelling doorgeven aan model?
        // of wordt die opstelling al meteen gezet wann het model (Othello / GameLogic) wordt aangemaakt
        gridPane.setStyle(preGameGridStyle);
    }

    public void loadPreGameBoardState() {
        System.out.println("PreGameBoardState Loaded!");
        Platform.runLater(() -> gridPane.getChildren().clear());
        Platform.runLater(this::loadGrid);
    }

    public void setOurTurn() {
        isOurTurn = true;
        Platform.runLater(() -> gridPane.setStyle(ourTurnGridStyle));
    }

    private void clickToDoMove(MouseEvent mouseEvent) {
    }
}
