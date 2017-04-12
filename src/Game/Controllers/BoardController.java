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
import javafx.geometry.HPos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
public class BoardController extends Board {
    public Othello othello;
    private static double cellWidth;
    private static double cellHeight;
    private static final int BOARDSIZE = 8;
    private BotInterface AI;
    private GameLogicInterface gameLogic;

    public BotInterface getAI() {
        return AI;
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

    public void initialize() {
        // othello = new Othello(); wordt al in de matchReceived gedaan?

        cellWidth = (gridPane.getPrefWidth() / BOARDSIZE) - 2;
        cellHeight = (gridPane.getPrefWidth() / BOARDSIZE) - 2;
        drawGrid(BOARDSIZE);
        loadGrid();
    }

    private void loadGrid() {
        int i;
        int j;
        for (i = 0; i < BOARDSIZE; i++) {
            for (j = 0; j < BOARDSIZE; j++) {
                Image image = new Image(BoardController.class.getClassLoader().getResourceAsStream("Empty.png"));
                ImageView imageView = new ImageView();
                imageView.setFitHeight(cellHeight - 5);
                imageView.setFitWidth(cellWidth - 5);
                imageView.setImage(image);
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
        gridPane.setStyle(preGameGridStyle);
    }

    public void loadPreGameBoardState() {
        System.out.println("PreGameBoardState Loaded!");
        Platform.runLater(() -> gridPane.getChildren().clear());
        Platform.runLater(this::loadGrid);
    }
}
