package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.*;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public final class ElevationProfileManager {
    ReadOnlyObjectProperty<ElevationProfile> elevationProfileRO;
    ReadOnlyDoubleProperty highlightedPosition;
    private final BorderPane borderPane;
   // private final Pane pane;

    private final ObjectProperty<Rectangle2D> rectangle = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> screenToWorldP = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> worldToScreenP = new SimpleObjectProperty<>();


    /**
     * Constructor
     *
     * @param elevationProfileRO
     * @param highlightedPosition
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> elevationProfileRO,
                                   ReadOnlyDoubleProperty highlightedPosition) throws NonInvertibleTransformException {


        this.elevationProfileRO = elevationProfileRO;
        this.highlightedPosition = highlightedPosition;
        this.borderPane = new BorderPane();
        borderPane.getStylesheets().add("elevation_profile.css");

        Insets insets = new Insets(10, 10, 20, 40);
        Pane pane = new Pane();



        Rectangle rect = new Rectangle(600 - (insets.getLeft() + insets.getRight()),
                300 - (insets.getTop() + insets.getBottom()));


      // rect.heightProperty().bind(pane.heightProperty());

        System.out.println(rect.getHeight() + " " + rect.getWidth());
        VBox vBox = new VBox();
        vBox.setId("profile_data");

        borderPane.setCenter(pane);
        borderPane.setBottom(vBox);

        Translate translation1 = Transform.translate(-insets.getLeft(), insets.getTop() + rect.getHeight());
        Scale s2 = Transform.scale(elevationProfileRO.get().length()/ rect.getWidth() ,
                -(elevationProfileRO.get().maxElevation() - elevationProfileRO.get().minElevation())
                                            /rect.getHeight());
        Translate translation2 = Transform.translate(4, 6);
        Translate translation1Inversed = translation1.createInverse();
        Scale sInversed = s2.createInverse();
       Translate translation2Inversed = translation2.createInverse();

        Affine aff = new Affine();
        aff.prependTranslation(translation1.getTx(), translation1.getTy());
        aff.prependScale(s2.getX(), s2.getY());
       aff.prependTranslation(translation2.getTx(), translation2.getTy());

        Affine affInversed = new Affine();
        affInversed.prependTranslation(translation2Inversed.getTx(), translation2Inversed.getTy());
        affInversed.prependScale(sInversed.getX(), sInversed.getY());
        affInversed.prependTranslation(translation1Inversed.getTx(), translation1Inversed.getTy());

        screenToWorldP.setValue(aff);
        worldToScreenP.setValue(affInversed);

        Map<Double, Double> map = new HashMap();

        for (double x = insets.getLeft(); x <= insets.getLeft() + rect.getWidth(); x++){

            double xValue = screenToWorldP.get().
                    transform(x, insets.getTop()).getX();
            double elevation = elevationProfileRO.get().elevationAt(xValue);


            map.put(xValue, elevation);
        }

        Polygon p = new Polygon();
        p.setId("profile");
        p.getPoints().addAll();
      //  p.setLayoutX(40 - p.getLayoutBounds().getMinX());
       // p.setLayoutY(10 - p.getLayoutBounds().getMinY());

        p.getPoints().addAll(insets.getLeft(), insets.getTop() + rect.getHeight());
        map.forEach((key, value) -> p.getPoints().addAll(key,value));
        p.getPoints().addAll(insets.getLeft() + rect.getWidth(), insets.getTop() + rect.getHeight());

        System.out.println(p.getPoints());

        borderPane.setCenter(pane);
        borderPane.setBottom(vBox);

        Text text = new Text();


        String s = "Longueur : %.1f km".formatted(elevationProfileRO.get().length() / 1000) +
                "     Montée : %.0f m".formatted(elevationProfileRO.get().totalAscent()) +
                "     Descente : %.0f m".formatted(elevationProfileRO.get().totalDescent()) +
                "     Altitude : de %.0f m à %.0f m".formatted(elevationProfileRO.get().minElevation(),
                        elevationProfileRO.get().maxElevation());

        text.setText(s);
        vBox.getChildren().add(text);



        // comment definir la taille du rectangle

      //  Rectangle2D r = new Rectangle2D(insets.getLeft(), insets.getTop(),
     //           elevationProfileRO.get().length(), elevationProfileRO.get().maxElevation());
    //    rectangle.setValue(r);

       // rect.get



/*
        pane.widthProperty().addListener(e -> {
            try {
                generationAffineFunctions();
            } catch (NonInvertibleTransformException ex) {
                ex.printStackTrace();
            }
        });
        pane.heightProperty().addListener(e -> {
            try {
                generationAffineFunctions();
            } catch (NonInvertibleTransformException ex) {
                ex.printStackTrace();
            }
        });

 */

        // translation en passant un point 2d



        // ajouter transfos a des nodes
     //   Rectangle rect = new Rectangle(50, 50, Color.RED);
     //   rect.getTransforms().add(new Rotate(45, 0, 0)); //rotate by 45 degrees


    }

    private void generationAffineFunctions() throws NonInvertibleTransformException {
        Translate translation1 = Transform.translate(3, 4);
        Scale s = Transform.scale(elevationProfileRO.get().length() / rectangle.get().getWidth(),
                elevationProfileRO.get().maxElevation() / rectangle.get().getHeight());
        Translate translation2 = Transform.translate(4, 6);
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

        //  screenToWorldP.get().transform()
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
