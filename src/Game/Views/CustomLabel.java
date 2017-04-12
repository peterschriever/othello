package Game.Views;

import javafx.scene.control.Label;

/**
 * Created by peterzen on 2017-04-12.
 * Part of the othello project.
 */
public class CustomLabel extends Label {
    private int x;
    private int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
