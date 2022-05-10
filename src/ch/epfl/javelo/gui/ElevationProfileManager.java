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
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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

    private final ObjectProperty<Rectangle2D> rectangle;
    private final ObjectProperty<Transform> screenToWorldP = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> worldToScreenP = new SimpleObjectProperty<>();
    private final DoubleProperty mousePositionOnProfileProperty = new SimpleDoubleProperty();



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
        this.borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");

        insets = new Insets(10, 10, 20, 40);

        pane = new Pane();


        rectangle = new SimpleObjectProperty<>();
       rectangle.setValue(Rectangle2D.EMPTY);
     //   rectangle.setValue(new Rectangle2D(insets.getLeft(), insets.getTop(),
     //           borderPane.getWidth() - (insets.getLeft() + insets.getRight()),
    //            borderPane.getHeight() -  - (insets.getTop() + insets.getBottom())));

       rectangle.bind(Bindings.createObjectBinding(() -> {
           double xValue = Math.max(0,pane.getWidth() - (insets.getLeft() + insets.getRight()));
           double yValue = Math.max(0, pane.getHeight() - (insets.getTop() + insets.getBottom()));
           return new Rectangle2D(insets.getLeft(), insets.getTop(),xValue,yValue);
       } ,pane.widthProperty(), pane.heightProperty()));


       rectangle.addListener(e -> {
           try {
               generationAffineFunctions();
               redrawPolygon();
           } catch (NonInvertibleTransformException ex) {
               ex.printStackTrace();
           }
       });

        VBox vBox = new VBox();
        vBox.setId("profile_data");

        borderPane.setCenter(pane);
        borderPane.setBottom(vBox);




    //    Map<Double, Double> map = new TreeMap<>();

      //  System.out.println(screenToWorldP.get().transform(40,10));
     //   System.out.println(worldToScreenP.get().transform(0,663));

        polygon = new Polygon();
        polygon.setId("profile");
        pane.getChildren().add(polygon);



        Line line = new Line();
       // System.out.println(highlightedPosition.getValue());
        //pane.setOnMouseMoved(e -> line.setLayoutX(Math2.clamp(insets.getLeft(),e.getX(), insets.getLeft() + rectangle.get().getWidth())));
       // pane.setOnMouseMoved(e -> {
         //           double value = Math2.clamp(insets.getLeft(), e.getX(), insets.getLeft() + rectangle.get().getWidth());
           //         mousePositionOnProfileProperty.set(screenToWorldP.get().transform(value, 0).getX());
             //   });

     //   mousePositionOnProfileProperty.addListener(e -> System.out.println(mousePositionOnProfileProperty.get()));
        //highlightedPosition.addListener(e ->{
      //     line.setLayoutX(worldToScreenP.get().transform(highlightedPosition.getValue(), 0).getX());
      //  });

        line.layoutXProperty().bind(Bindings.createDoubleBinding(() -> worldToScreenP.get().transform(highlightedPosition.getValue(), 0).getX(),
                 highlightedPosition));
        line.startYProperty().bind(Bindings.select(rectangle, "minY"));
        line.endYProperty().bind(Bindings.select(rectangle, "maxY"));
        //line.visibleProperty().bind(Bindings.createBooleanBinding()highlightedPosition.greaterThanOrEqualTo(0).get());
        pane.getChildren().add(line);


        Text text = new Text();
        String s = "Longueur : %.1f km".formatted(elevationProfileRO.get().length() / 1000) +
                "     Montée : %.0f m".formatted(elevationProfileRO.get().totalAscent()) +
                "     Descente : %.0f m".formatted(elevationProfileRO.get().totalDescent()) +
                "     Altitude : de %.0f m à %.0f m".formatted(elevationProfileRO.get().minElevation(),
                        elevationProfileRO.get().maxElevation());

        text.setText(s);
        vBox.getChildren().add(text);

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
