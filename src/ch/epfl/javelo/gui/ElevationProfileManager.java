package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.*;

import java.util.Map;
import java.util.TreeMap;

import static java.lang.Double.NaN;

public final class ElevationProfileManager{
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfile;
    private final DoubleProperty highlightedPosition;

    private final ObjectProperty<Rectangle2D> rectangle;
    private final ObjectProperty<Transform> screenToWorldP;
    private final ObjectProperty<Transform> worldToScreenP;
    private final DoubleProperty mousePositionOnProfileProperty;

    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileRO,
                                   ReadOnlyDoubleProperty highlightedPosition){
        this.elevationProfile = elevationProfileRO;
        this.highlightedPosition = (DoubleProperty) highlightedPosition;
        this.rectangle = new SimpleObjectProperty<>();
        this.screenToWorldP = new SimpleObjectProperty<>();
        this.worldToScreenP = new SimpleObjectProperty<>();
        this.mousePositionOnProfileProperty = new SimpleDoubleProperty();

        this.borderPane = createGui();
        addBindings();
        addListeners();
    }


    private void addBindings(){
        rectangle.bind(Bindings.createObjectBinding( () ->
                        {double xValue = Math.max(0,pane.getWidth() - (insets.getLeft() + insets.getRight()));
                            double yValue = Math.max(0, pane.getHeight() - (insets.getTop() + insets.getBottom()));
                            return new Rectangle2D(insets.getLeft(), insets.getTop(), xValue, yValue); },
                        pane.widthProperty(),
                        pane.heightProperty())
        );
    }

    private void addListeners(){
        pane.setOnMouseMoved(e -> {
            double value = screenToWorldP.get().transform(e.getX(),0).getX();
            if ((value >= 0 && value <= elevationProfile.get().length())){
                mousePositionOnProfileProperty.setValue(value);
            }
            else{
                mousePositionOnProfileProperty.setValue(NaN);
            }
        });

        pane.setOnMouseExited(e -> mousePositionOnProfileProperty.setValue(NaN));

        rectangle.addListener(e -> {
            try {
                generateNewAffineFunctions();
                redrawProfile();
                drawGridAndLabels();
                highlightedPositionLine.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                                worldToScreenP.get().transform(
                                        this.highlightedPosition.getValue(), 0).getX(),
                        this.highlightedPosition, worldToScreenP));
                highlightedPositionLine.startYProperty().bind(Bindings.select(rectangle, "minY"));
                highlightedPositionLine.endYProperty().bind(Bindings.select(rectangle, "maxY"));
                highlightedPositionLine.visibleProperty().bind(highlightedPosition.greaterThanOrEqualTo(0));
            } catch (NonInvertibleTransformException ex) {
                ex.printStackTrace();
            }
        });

    }

    /**
     * returns the pane of the ElevationProfileManager
     */
    public Pane pane() {
        return borderPane;
    }

    /**
     * Returns a read-only property containing the position of the mouse pointer along the profile
     * @return the position (in meters, rounded to the nearest integer), or NaN if the mouse pointer is not above the profile
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {
        return mousePositionOnProfileProperty;
    }


//----------------------------------Section for Creating and Drawing GUI Groups----------------------------------

    private final BorderPane borderPane;
    private Pane pane;
    private Polygon profileGraph;
    private Line highlightedPositionLine;
    private Path grid;
    private Group gridLabels;
    private Insets insets;

    private BorderPane createGui(){
        BorderPane borderPane = new BorderPane();
        insets = new Insets(10, 10, 20, 40);
        borderPane.getStylesheets().add("elevation_profile.css");
        VBox profileDataBox = createVBox();
        profileDataBox.setId("profile_data");
        borderPane.setCenter(createInternalPane());
        borderPane.setBottom(profileDataBox);
        return borderPane;
    }
    private VBox createVBox(){
        VBox profileDataBox = new VBox();
        Text text = new Text();

        String s = "Longueur : %.1f km".formatted(elevationProfile.get().length() / 1000) +
                "     Montée : %.0f m".formatted(elevationProfile.get().totalAscent()) +
                "     Descente : %.0f m".formatted(elevationProfile.get().totalDescent()) +
                "     Altitude : de %.0f m à %.0f m".formatted(elevationProfile.get().minElevation(),
                        elevationProfile.get().maxElevation());

        text.setText(s);
        profileDataBox.getChildren().add(text);

        return profileDataBox;
    }
    private Pane createInternalPane(){
        pane = new Pane();
        grid = new Path();
        grid.setId("grid");
        profileGraph = new Polygon();
        profileGraph.setId("profile");
        gridLabels = createGridLabelsGroup();
        highlightedPositionLine = new Line();
        pane.getChildren().addAll(grid, profileGraph, gridLabels, highlightedPositionLine);
        return pane;
    }
    private Group createGridLabelsGroup(){
        gridLabels = new Group();
        Text horizontalText = new Text();
        horizontalText.getStyleClass().addAll("grid_label", "horizontal");
        Text verticalText = new Text();
        verticalText.getStyleClass().addAll("grid_label", "vertical");
        gridLabels.getChildren().addAll(horizontalText, verticalText);
        return gridLabels;
    }


    private void redrawProfile(){
        profileGraph.getPoints().clear();
        Map<Double, Double> map = new TreeMap<>();

        for (double x = insets.getLeft(); x <= insets.getLeft() + rectangle.get().getWidth(); x++) {
            double xValue = screenToWorldP.get().transform(x, 0).getX();
            double elevation = elevationProfile.get().elevationAt(xValue);
            double yValue = worldToScreenP.get().transform(0, elevation).getY();
            map.put(x, yValue);
        }

        profileGraph.getPoints().addAll(insets.getLeft(), insets.getTop() + rectangle.get().getHeight());
        map.forEach((key, value) -> profileGraph.getPoints().addAll(key, value));
        profileGraph.getPoints().addAll(insets.getLeft() + rectangle.get().getWidth(), insets.getTop() + rectangle.get().getHeight());
    }

    private void drawGridAndLabels(){
        int[] ELE_STEPS = { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
        int[] POS_STEPS = { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };

        grid.getElements().clear();
        gridLabels.getChildren().clear();

        double nbPixelsPerMeterY = rectangle.get().getHeight() /
                (elevationProfile.get().maxElevation() - elevationProfile.get().minElevation());
        int horizontalSpacing = chooseSpaceBetweenLines(nbPixelsPerMeterY, ELE_STEPS, 25);
        double y_meters = 0;
        int firstHeight = (int) (horizontalSpacing *
                Math.ceil(elevationProfile.get().minElevation() / horizontalSpacing));

        while(y_meters < elevationProfile.get().maxElevation()){
            double y_pixels = worldToScreenP.get().transform(0, y_meters).getY();
            y_meters += horizontalSpacing;

            if (y_pixels < insets.getTop() + rectangle.get().getHeight()) {
                PathElement lineExtremity1 = new MoveTo(insets.getLeft(), y_pixels);
                PathElement lineExtremity2 = new LineTo(insets.getLeft() + rectangle.get().getWidth(), y_pixels);
                grid.getElements().addAll(lineExtremity1, lineExtremity2);

                Text text = new Text();
                text.setTextOrigin(VPos.CENTER);
                text.setX(insets.getLeft()/2);
                text.setY(y_pixels);
                text.setText(Integer.toString(firstHeight));
                text.setFont(Font.font("Avenir", 10));
                gridLabels.getChildren().add(text);
                firstHeight += horizontalSpacing;
            }
        }

        double nbPixelsPerMeterX = rectangle.get().getWidth() / elevationProfile.get().length();
        int verticalSpacing = chooseSpaceBetweenLines(nbPixelsPerMeterX, POS_STEPS, 50);
        int length = 0;
        while(length < elevationProfile.get().length()){
            double pixelsX = worldToScreenP.get().transform(length, 0).getX();
            PathElement lineExtremity1 = new MoveTo(pixelsX, insets.getTop() + rectangle.get().getHeight());
            PathElement lineExtremity2 = new LineTo(pixelsX, insets.getTop());
            grid.getElements().addAll(lineExtremity1, lineExtremity2);
            length += verticalSpacing;

            Text text = new Text();
            text.setTextOrigin(VPos.TOP);
            text.setX(pixelsX - 3);
            text.setY(insets.getTop() + rectangle.get().getHeight());
            text.setText(Integer.toString((length / verticalSpacing) - verticalSpacing/1000));
            text.setFont(Font.font("Avenir", 10));
            gridLabels.getChildren().add(text);
        }
    }


//---------------------------------------Section for relative position methods---------------------------------------

    private void generateNewAffineFunctions() throws NonInvertibleTransformException {
        /*
            translate the rectangle origin to borderpane origin
            scale rectangle to borderpane
            reposition rectangle to original position
        */
        Affine screenToWorld = new Affine();
        screenToWorld.prependTranslation(-insets.getLeft(), -insets.getTop());
        screenToWorld.prependScale(elevationProfile.get().length() / rectangle.get().getWidth(),
                -(elevationProfile.get().maxElevation() - elevationProfile.get().minElevation())
                        / rectangle.get().getHeight()
        );
        screenToWorld.prependTranslation(0, elevationProfile.get().maxElevation());

        screenToWorldP.setValue(screenToWorld);
        worldToScreenP.setValue(screenToWorld.createInverse());
    }

    private int chooseSpaceBetweenLines(double pixelsPerMeter,int[] steps, int minimalDistance){
        int space = steps[steps.length - 1];
        for (int spacing: steps) {
            double value = spacing * pixelsPerMeter;
            if(value >= minimalDistance){
                space = spacing;
                break;
            }
        }
        return space;
    }
}