package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;

/**
 *
 */
public final class ElevationProfileManager {
    ReadOnlyObjectProperty<ElevationProfile> elevationProfileRO;
    ReadOnlyDoubleProperty highlightedPosition;
    private final Pane pane;

    /**
     * Constructor
     * @param elevationProfileRO
     * @param highlightedPosition
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileRO,
                                   ReadOnlyDoubleProperty highlightedPosition) {
        Insets insets = new Insets(10, 10, 20, 40);
        this.elevationProfileRO = elevationProfileRO;
        this.highlightedPosition = highlightedPosition;
        pane = createGroups();

    }

    /**
     * returns the pane of the ElevationProfileManager
     */
    public Pane pane(){
        return pane;
    }

    /**
     * Returns a read-only property containing the position of the mouse pointer along the profile
     * @return the position (in meters, rounded to the nearest integer), or NaN if the mouse pointer is not above the profile
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        System.out.println(pane.getChildren());
        return new SimpleDoubleProperty(Double.NaN);
    }

    private Pane createGroups(){
        BorderPane borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");

        VBox profileDataBox = new VBox();
        profileDataBox.setId("profile_data");

        borderPane.setCenter(createInternalPane());
        borderPane.setBottom(profileDataBox);

        return borderPane;
    }

    private Pane createInternalPane(){
        Pane pane = new Pane();

        Path grid = new Path();
        grid.setId("grid");

        Polygon profileGraph = new Polygon();
        profileGraph.setId("profile");

        Group gridLabels = createGridLabelsGroup();
        Line highlightedPositionLine = new Line();

        pane.getChildren().addAll(grid, profileGraph, gridLabels, highlightedPositionLine);
        return pane;
    }

    private Group createGridLabelsGroup(){
        Group gridLabels = new Group();

        Text horizontalText = new Text();
        horizontalText.getStyleClass().addAll("grid_label", "horizontal");

        Text verticalText = new Text();
        verticalText.getStyleClass().addAll("grid_label", "vertical");

        gridLabels.getChildren().addAll(horizontalText, verticalText);
        return gridLabels;
    }


}
