package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.Float.NaN;

/**
 *
 */
public final class ElevationProfileManager {
    ReadOnlyObjectProperty<ElevationProfile> elevationProfileRO;
    DoubleProperty highlightedPosition;
    private final BorderPane borderPane;
    private final Pane pane;
    private final Polygon polygon;
    private final Insets insets;
    private final Line line;


    private final ObjectProperty<Rectangle2D> rectangle;
    private final ObjectProperty<Transform> screenToWorldP = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> worldToScreenP = new SimpleObjectProperty<>();
    private final DoubleProperty mousePositionOnProfileProperty;



    /**
     * Constructor
     *
     * @param elevationProfileRO
     * @param highlightedPosition
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileRO,
                                   ReadOnlyDoubleProperty highlightedPosition) throws NonInvertibleTransformException {


        this.elevationProfileRO = elevationProfileRO;
        this.highlightedPosition = (DoubleProperty) highlightedPosition;
        mousePositionOnProfileProperty = new SimpleDoubleProperty();
        line = new Line();
        this.borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");

        insets = new Insets(10, 10, 20, 40);
        pane = new Pane();

        pane.setOnMouseMoved(e -> {
            double value = screenToWorldP.get().transform(e.getX(),0).getX();
            if ((value >= 0 && value <= elevationProfileRO.get().length())){
                mousePositionOnProfileProperty.setValue(value);
            }
            else{
                mousePositionOnProfileProperty.setValue(NaN);
            }

        });

        rectangle = new SimpleObjectProperty<>();
        rectangle.setValue(Rectangle2D.EMPTY);

     //   rectangle.setValue(new Rectangle2D(insets.getLeft(), insets.getTop(),
     //           borderPane.getWidth() - (insets.getLeft() + insets.getRight()),
    //            borderPane.getHeight() -  - (insets.getTop() + insets.getBottom())));
       rectangle.setValue(Rectangle2D.EMPTY);

       rectangle.bind(Bindings.createObjectBinding(() -> {
           double xValue = Math.max(0,pane.getWidth() - (insets.getLeft() + insets.getRight()));
           double yValue = Math.max(0, pane.getHeight() - (insets.getTop() + insets.getBottom()));
           return new Rectangle2D(insets.getLeft(), insets.getTop(),xValue,yValue);
       } ,pane.widthProperty(), pane.heightProperty())
       );


       rectangle.addListener(e -> {
           try {
               generationAffineFunctions();
               redrawPolygon();
               line.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                             worldToScreenP.get().transform(highlightedPosition.getValue(), 0).getX(),
                       highlightedPosition, worldToScreenP));
               line.startYProperty().bind(Bindings.select(rectangle, "minY"));
               line.endYProperty().bind(Bindings.select(rectangle, "maxY"));
           } catch (NonInvertibleTransformException ex) {
               ex.printStackTrace();
           }
       });

        VBox vBox = new VBox();
        vBox.setId("profile_data");

        borderPane.setCenter(pane);
        borderPane.setBottom(vBox);

        polygon = new Polygon();
        polygon.setId("profile");
        pane.getChildren().add(polygon);



        Line line = new Line();



        line.visibleProperty().bind(highlightedPosition.greaterThanOrEqualTo(0));


        pane.getChildren().add(line);


        Text text = new Text();
        String s = "Longueur : %.1f km".formatted(elevationProfileRO.get().length() / 1000) +
                "     Montée : %.0f m".formatted(elevationProfileRO.get().totalAscent()) +
                "     Descente : %.0f m".formatted(elevationProfileRO.get().totalDescent()) +
                "     Altitude : de %.0f m à %.0f m".formatted(elevationProfileRO.get().minElevation(),
                        elevationProfileRO.get().maxElevation());

        text.setText(s);
        vBox.getChildren().add(text);
        generationAffineFunctions();

        drawGrid(pane);

    }


    private void generationAffineFunctions() throws NonInvertibleTransformException {
        Translate translation1 = Transform.translate(-insets.getLeft(), -insets.getTop());
        Scale s = Transform.scale(elevationProfileRO.get().length() / rectangle.get().getWidth(),
                -(elevationProfileRO.get().maxElevation() - elevationProfileRO.get().minElevation()) / rectangle.get().getHeight());
        Translate translation2 = Transform.translate(0, elevationProfileRO.get().maxElevation());
        Translate translation1Inversed = translation1.createInverse();
        Scale sInversed = s.createInverse();
        Translate translation2Inversed = translation2.createInverse();

        Affine aff = new Affine();
        aff.prependTranslation(translation1.getTx(), translation1.getTy());
        aff.prependScale(s.getX(), s.getY());
        aff.prependTranslation(translation2.getTx(), translation2.getTy());

        Affine affInversed = new Affine();
        affInversed.prependTranslation(translation2Inversed.getTx(), translation2Inversed.getTy());
        affInversed.prependScale(sInversed.getX(), sInversed.getY());
        affInversed.prependTranslation(translation1Inversed.getTx(), translation1Inversed.getTy());

        screenToWorldP.setValue(aff);
        worldToScreenP.setValue(affInversed);

    }

    private void redrawPolygon(){
        polygon.getPoints().clear();
        Map<Double, Double> map = new TreeMap<>();

        for (double x = insets.getLeft(); x <= insets.getLeft() + rectangle.get().getWidth(); x++) {
            double xValue = screenToWorldP.get().
                    transform(x, 0).getX();
            double elevation = elevationProfileRO.get().elevationAt(xValue);
            double yValue = worldToScreenP.get().transform(0, elevation).getY();
            map.put(x, yValue);
        }

        polygon.getPoints().addAll(insets.getLeft(), insets.getTop() + rectangle.get().getHeight());
        map.forEach((key, value) -> polygon.getPoints().addAll(key, value));
        polygon.getPoints().addAll(insets.getLeft() + rectangle.get().getWidth(), insets.getTop() + rectangle.get().getHeight());
    }

    private void drawGrid(Pane pane){
        Path grid = new Path();
        grid.setId("grid");

        //calculate distance between lines
        //vertical:
        int[] vertical = { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
        double verticalSpacing;
        for (double spacing: vertical) {

            if(worldToScreenP.get().deltaTransform(new Point2D(elevationProfileRO.get().length()/spacing, 0)).getX() >= 50){
                verticalSpacing = spacing;
                break;
            }
        }

        int[] horizontal = { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
        double horizontalSpacing = horizontal[horizontal.length -1];
        for (double spacing: horizontal) {
            if(worldToScreenP.get().deltaTransform(new Point2D(0, elevationProfileRO.get().maxElevation()/spacing)).getY() >= 25){
                horizontalSpacing = spacing;
                break;
            }
        }

        double horizontalCoordinate = 0;
        while(horizontalCoordinate < elevationProfileRO.get().length()){
            double x_pixels = worldToScreen(horizontalCoordinate,0).getX();

            PathElement lineExtremity1 = new MoveTo(x_pixels, 0);
            PathElement lineExtremity2 = new LineTo(x_pixels, worldToScreen(0, elevationProfileRO.get().maxElevation()).getY());
            grid.getElements().addAll(lineExtremity1, lineExtremity2);
            horizontalCoordinate += horizontalSpacing;
        }


        pane.getChildren().add(grid);

    }

    private Point2D worldToScreen(double x, double y){
        return worldToScreenP.get().deltaTransform(new Point2D(x, y));
    }

    /**
     * returns the pane of the ElevationProfileManager
     */
    public Pane pane() {
        return borderPane;
    }

    /**
     * Returns a read-only property containing the position of the mouse pointer along the profile
     *
     * @return the position (in meters, rounded to the nearest integer), or NaN if the mouse pointer is not above the profile
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty() {

        // System.out.println(pane.getChildren());
        //  System.out.println(((Pane) pane.getChildren().get(0)).getChildren());
        return new SimpleDoubleProperty(Double.NaN);
        //return mousePositionOnProfileProperty;
    }
}

    /*
    private Pane createGroups(){
        BorderPane borderPane = new BorderPane();

       // insets.getLeft()
        //BorderPane.setMargin(borderPane, insets);
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

     */
