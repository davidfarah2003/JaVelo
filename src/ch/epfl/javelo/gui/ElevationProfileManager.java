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

/**
 * Class that is responsible for drawing elevation profile of the route on GUI
 */
public final class ElevationProfileManager{
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfile;
    private final DoubleProperty highlightedPosition;

    private final ObjectProperty<Rectangle2D> rectangle;
    private final ObjectProperty<Transform> screenToWorldP;
    private final ObjectProperty<Transform> worldToScreenP;
    private final DoubleProperty mousePositionOnProfileProperty;

    private final BorderPane borderPane;
    private final Pane pane = new Pane();
    private final Polygon profileGraph = new Polygon();
    private final Line highlightedPositionLine = new Line();
    private final Path grid = new Path();
    private final Group gridLabels = new Group();
    private static final Insets insets = new Insets(10, 10, 20, 40);
    private VBox vBox;
    /**
     * Constructor that initializes the GUI and creates bindings and listeners
     * @param elevationProfileRO Elevation profile of the Route (Read Only Property)
     * @param highlightedPosition Currently highlighted position
     */
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


    /**
     * Add bindings between rectangle and pane
     */
    private void addBindings(){
        rectangle.bind(Bindings.createObjectBinding( () ->
                        {double xValue = Math.max(0,pane.getWidth() - (insets.getLeft() + insets.getRight()));
                            double yValue = Math.max(0, pane.getHeight() - (insets.getTop() + insets.getBottom()));
                            return new Rectangle2D(insets.getLeft(), insets.getTop(), xValue, yValue); },
                        pane.widthProperty(),
                        pane.heightProperty())
        );
    }

    /**
     * Add listeners to the pane and rectangle
     */
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

        elevationProfile.addListener(e ->{
            if (elevationProfile.get() != null) {
                try {
                    generateNewAffineFunctions();
                } catch (NonInvertibleTransformException ex) {
                    ex.printStackTrace();
                }
                redrawProfile();
                drawGridAndLabels();
                createProfileDataBox();
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


//----------------------------------Section for Creating and Drawing GUI elements----------------------------------

    /**
     * Creates the GUI structure that will display the elevation Profile
     * @return return the BorderPane that is on the root of the structure
     */
    private BorderPane createGui(){
        BorderPane borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");
        vBox =  new VBox();
        vBox.setId("profile_data");
        borderPane.setCenter(initializeInternalPane());
        borderPane.setBottom(vBox);
        return borderPane;
    }

    /**
     * @return the VBox that contains ElevationProfile information of the route
     */
    private void createProfileDataBox(){

            vBox.getChildren().clear();
            Text text = new Text();
            String s = "Longueur : %.1f km".formatted(elevationProfile.get().length() / 1000) +
                    "     Montée : %.0f m".formatted(elevationProfile.get().totalAscent()) +
                    "     Descente : %.0f m".formatted(elevationProfile.get().totalDescent()) +
                    "     Altitude : de %.0f m à %.0f m".formatted(elevationProfile.get().minElevation(),
                            elevationProfile.get().maxElevation());

            text.setText(s);
            vBox.getChildren().add(text);


    }

    /**
     * Initializes the internal Pane of that will contain the grid and graph
     * @return the pane after initializing
     */
    private Pane initializeInternalPane(){
        grid.setId("grid");
        profileGraph.setId("profile");
        initializeGridLabelsGroup();
        pane.getChildren().addAll(grid, profileGraph, initializeGridLabelsGroup(), highlightedPositionLine);
        return pane;
    }

    /**
     * Initializes the Grid Labels Group
     * @return the gridLabels Group after initializing
     */
    private Group initializeGridLabelsGroup(){
        Text horizontalText = new Text();
        horizontalText.getStyleClass().addAll("grid_label", "horizontal");
        Text verticalText = new Text();
        verticalText.getStyleClass().addAll("grid_label", "vertical");
        gridLabels.getChildren().addAll(horizontalText, verticalText);
        return gridLabels;
    }

    /**
     * Redraw the Profile Graph
     */
    private void redrawProfile() {
        profileGraph.getPoints().clear();
        Map<Double, Double> map = new TreeMap<>();

            for (double x = insets.getLeft(); x < insets.getLeft() + rectangle.get().getWidth(); x++) {
                double xValue = screenToWorldP.get().transform(x, 0).getX();
                double elevation = elevationProfile.get().elevationAt(xValue);
                double yValue = worldToScreenP.get().transform(0, elevation).getY();
                map.put(x, yValue);
            }

            profileGraph.getPoints().addAll(insets.getLeft(), insets.getTop() + rectangle.get().getHeight());
            map.forEach((key, value) -> profileGraph.getPoints().addAll(key, value));
            profileGraph.getPoints().addAll(insets.getLeft() + rectangle.get().getWidth(),
                                                insets.getTop() + rectangle.get().getHeight());

    }

    /**
     * Draws Grid and it's labels
     */
    private void drawGridAndLabels(){
        int[] ELE_STEPS = { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
        int[] POS_STEPS = { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };

        grid.getElements().clear();
        gridLabels.getChildren().clear();

        double nbPixelsPerMeterY = rectangle.get().getHeight() /
                (elevationProfile.get().maxElevation() - elevationProfile.get().minElevation());
        int spaceBetweenHorizontalLines = chooseSpaceBetweenLines(nbPixelsPerMeterY, ELE_STEPS, 25);
        double y_meters = 0;
        int firstHeight = (int) (spaceBetweenHorizontalLines *
                Math.ceil(elevationProfile.get().minElevation() / spaceBetweenHorizontalLines));

        while(y_meters < elevationProfile.get().maxElevation()){
            double y_pixels = worldToScreenP.get().transform(0, y_meters).getY();
            y_meters += spaceBetweenHorizontalLines;

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
                firstHeight += spaceBetweenHorizontalLines;
            }
        }

        double nbPixelsPerMeterX = rectangle.get().getWidth() / elevationProfile.get().length();
        int spaceBetweenVerticalLines = chooseSpaceBetweenLines(nbPixelsPerMeterX, POS_STEPS, 50);

        int length = 0;
        int i = 0;
        while(length < elevationProfile.get().length()){
            double pixelsX = worldToScreenP.get().transform(length, 0).getX();
            PathElement lineExtremity1 = new MoveTo(pixelsX, insets.getTop() + rectangle.get().getHeight());
            PathElement lineExtremity2 = new LineTo(pixelsX, insets.getTop());
            grid.getElements().addAll(lineExtremity1, lineExtremity2);
            length += spaceBetweenVerticalLines;

            Text text = new Text();
            text.setTextOrigin(VPos.TOP);
            text.setX(pixelsX - 2);
            text.setY(insets.getTop() + rectangle.get().getHeight());
            text.setText(Integer.toString((spaceBetweenVerticalLines/1000)* i));
            text.setFont(Font.font("Avenir", 10));
            gridLabels.getChildren().add(text);
            i++;
        }
    }


//---------------------------------------Section for relative position methods---------------------------------------

    /**
     * Generates Affine properties that convert from Screen to World
     * @throws NonInvertibleTransformException when the transformation is not invertible (it is in our case)
     */
    private void generateNewAffineFunctions() throws NonInvertibleTransformException {
        /*  translate the rectangle origin to borderpane origin
            scale rectangle to borderpane
            reposition rectangle to original position  */

        if (elevationProfile.get() != null) {
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
    }

    /**
     * @param pixelsPerMeter number of pixels per meter in the direction
     * @param steps Different spacing possible for this direction
     * @param minimalDistance Minimal distance between 2 values
     * @return the spacing between 2 positions
     */
    private int chooseSpaceBetweenLines(double pixelsPerMeter, int[] steps, int minimalDistance){
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