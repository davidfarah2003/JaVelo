package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Handles Errors signaled from the gui
 * @author Wesley Nana Davies (344592)
 * @author David Farah (341017)
 */
public final class ErrorManager {
    private final VBox vBox;
    private SequentialTransition sequentialTransition;

    /**
     * Constructor
     */
    public ErrorManager(){
        vBox = new VBox();
        vBox.setMouseTransparent(true);
        vBox.getStylesheets().add("error.css");
        sequentialTransition = new SequentialTransition();
    }

    /**
     * returns the pane of error
     * @return the pane (vBox)
     */
    public Pane pane(){
        return vBox;
    }

    /**
     * Display an error on the screen
     * @param s the error to display
     */
    public void displayError(String s){
        sequentialTransition.stop();
        vBox.getChildren().clear();

        Text t = new Text();
        t.setText(s);
        vBox.getChildren().add(t);


        FadeTransition f1 = new FadeTransition(Duration.valueOf("200ms"));
        f1.setFromValue(0);
        f1.setToValue(0.8);

        PauseTransition pauseTransition = new PauseTransition(Duration.valueOf("2000ms"));

        FadeTransition f2 = new FadeTransition(Duration.valueOf("500ms"));
        f2.setFromValue(0.8);
        f2.setToValue(0);

        SequentialTransition st = new SequentialTransition(vBox, f1, pauseTransition,f2);
        sequentialTransition = st;

        sequentialTransition.play();
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
}
