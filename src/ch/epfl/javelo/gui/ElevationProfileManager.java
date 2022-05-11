package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.*;
import java.util.Map;
import java.util.TreeMap;
import static java.lang.Double.NaN;

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
    private final Path grid;


    private final ObjectProperty<Rectangle2D> rectangle;
    private final ObjectProperty<Transform> screenToWorldP;
    private final ObjectProperty<Transform> worldToScreenP;
    private final DoubleProperty mousePositionOnProfileProperty;



    /**
     * Constructor
     *
     * @param elevationProfileRO
     * @param highlightedPosition
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileRO,
                                   ReadOnlyDoubleProperty highlightedPosition){


        this.elevationProfileRO = elevationProfileRO;
        this.highlightedPosition = (DoubleProperty) highlightedPosition;
        borderPane = new BorderPane();
        pane = new Pane();
        VBox vBox = new VBox();
        vBox.setId("profile_data");

        borderPane.setCenter(pane);
        borderPane.setBottom(vBox);

        polygon = new Polygon();
        polygon.setId("profile");
        line = new Line();
        pane.getChildren().addAll(polygon, line);
        borderPane.getStylesheets().add("elevation_profile.css");
        insets = new Insets(10, 10, 20, 40);
        rectangle = new SimpleObjectProperty<>();
        screenToWorldP = new SimpleObjectProperty<>();
        worldToScreenP = new SimpleObjectProperty<>();
        mousePositionOnProfileProperty = new SimpleDoubleProperty();

        pane.setOnMouseMoved(e -> {
            double value = screenToWorldP.get().transform(e.getX(),0).getX();
            if ((value >= 0 && value <= elevationProfileRO.get().length())){
                mousePositionOnProfileProperty.setValue(value);
            }
            else{
                mousePositionOnProfileProperty.setValue(NaN);
            }
        });

        pane.setOnMouseExited(e -> mousePositionOnProfileProperty.setValue(NaN));

       rectangle.bind(Bindings.createObjectBinding(() -> {
           double xValue = Math.max(0,pane.getWidth() - (insets.getLeft() + insets.getRight()));
           double yValue = Math.max(0, pane.getHeight() - (insets.getTop() + insets.getBottom()));
           return new Rectangle2D(insets.getLeft(), insets.getTop(), xValue, yValue);
       } , pane.widthProperty(), pane.heightProperty()));


       rectangle.addListener(e -> {
           try {
               generateNewAffineFunctions();
               redrawPolygon();
               drawGrid();
               drawLabels();
               line.layoutXProperty().bind(Bindings.createDoubleBinding(() ->
                               worldToScreenP.get().transform(this.highlightedPosition.getValue(), 0).getX(),
                       this.highlightedPosition, worldToScreenP));
               line.startYProperty().bind(Bindings.select(rectangle, "minY"));
               line.endYProperty().bind(Bindings.select(rectangle, "maxY"));
               line.visibleProperty().bind(highlightedPosition.greaterThanOrEqualTo(0));

           } catch (NonInvertibleTransformException ex) {
               ex.printStackTrace();
           }
       });


        Text text = new Text();

        String s = "Longueur : %.1f km".formatted(elevationProfileRO.get().length() / 1000) +
                "     Montée : %.0f m".formatted(elevationProfileRO.get().totalAscent()) +
                "     Descente : %.0f m".formatted(elevationProfileRO.get().totalDescent()) +
                "     Altitude : de %.0f m à %.0f m".formatted(elevationProfileRO.get().minElevation(),
                        elevationProfileRO.get().maxElevation());


        text.setText(s);
        vBox.getChildren().add(text);

        //Ainsi, les lignes verticales correspondant à la position peuvent être dessinées tous les
        //1, 2, 5, 10, 25, 50 ou 100 km, tandis que les lignes horizontales correspondant à
        // l'altitude peuvent être dessinées tous les 5, 10, 20, 25, 50, 100, 200, 250, 500 ou 1000 m.
        // La valeur utilisée dans les deux cas est la plus petite garantissant que, à l'écran,
        // les lignes horizontales soient distantes d'au moins 25 unités JavaFX (pixels),
        // et les verticales d'au moins 50 unités.
        // (Si aucune valeur ne permet de garantir cela, la plus grande de toutes est utilisée.)


        grid = new Path();
        grid.setId("grid");
        pane.getChildren().add(grid);






    }


    private void drawLabels(){

    }
    private void drawGrid() {
        grid.getElements().clear();

        double numberOfPixelsPerMeterY = rectangle.get().getHeight() /
                (elevationProfileRO.get().maxElevation() - elevationProfileRO.get().minElevation());

        int[] ELE_STEPS = { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };
        double spaceBetweenHorizontalLines = ELE_STEPS[ELE_STEPS.length -1];
        for (double spacing: ELE_STEPS) {
            double value = spacing * numberOfPixelsPerMeterY;
            if(value >= 25){
                spaceBetweenHorizontalLines = spacing;
                break;
            }
        }

        double height = 0;
        while(height < elevationProfileRO.get().maxElevation()){
            double y_pixels = worldToScreenP.get().transform(0, height).getY();
            height += spaceBetweenHorizontalLines;

            if (y_pixels <= insets.getTop() + rectangle.get().getHeight()) {
                PathElement lineExtremity1 = new MoveTo(insets.getLeft(), y_pixels);
                PathElement lineExtremity2 = new LineTo(insets.getLeft() + rectangle.get().getWidth(), y_pixels);
                grid.getElements().addAll(lineExtremity1, lineExtremity2);
            }

        }


        double numberOfPixelsPerMeterX = rectangle.get().getWidth() / elevationProfileRO.get().length();
        int[] POS_STEPS = { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
        double spaceBetweenVerticalLines = POS_STEPS[POS_STEPS.length -1];

        for (double spacing: POS_STEPS) {
            double value = spacing * numberOfPixelsPerMeterX;
            if(value >= 50){
                spaceBetweenVerticalLines = spacing;
                break;
            }
        }

        double length = 0;
        while(length < elevationProfileRO.get().length()){
            double x_pixels = worldToScreenP.get().transform(length, 0).getX();
            System.out.println(x_pixels);
            PathElement lineExtremity1 = new MoveTo(x_pixels, insets.getTop() + rectangle.get().getHeight());
            PathElement lineExtremity2 = new LineTo(x_pixels, insets.getTop());
            grid.getElements().addAll(lineExtremity1, lineExtremity2);
            length += spaceBetweenVerticalLines;
        }

    }







    private void generateNewAffineFunctions() throws NonInvertibleTransformException {
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
                double xValue = screenToWorldP.get().transform(x, 0).getX();
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
        return mousePositionOnProfileProperty;
    }
}
