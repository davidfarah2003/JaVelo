package ch.epfl.javelo.gui;

import ch.epfl.javelo.LRUCache;
import ch.epfl.javelo.routing.*;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public final class RouteBean {
    private final RouteComputer routeComputer;
    public static ObservableList<Waypoint> waypoints;
    private final ObjectProperty<Route> route;
    private final DoubleProperty highlightedPosition = new SimpleDoubleProperty(Double.NaN);
    private final ObjectProperty<ElevationProfile> elevationProfile;
    private final Map<Integer, Route> hashRouteMap = new LRUCache<>(5, 0.75f);

    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        waypoints = FXCollections.observableArrayList();
        route = new SimpleObjectProperty<>();
        elevationProfile = new SimpleObjectProperty<>();

        waypoints.addListener((InvalidationListener) e -> {
             if (waypoints.size() > 0)
                recalculateRouteAndProfile();
        });
}


    private void recalculateRouteAndProfile(){
        if (waypoints.size() == 1){
            route.setValue(null);
            elevationProfile.setValue(null);
            return;
        }

        List<Route> singleRoutes = new ArrayList<>();
        Iterator<Waypoint> it = waypoints.listIterator();
        Waypoint oldWaypoint = it.next();
        Waypoint currentWaypoint;

        while (it.hasNext()){
            currentWaypoint = it.next();

            if(!currentWaypoint.equals(oldWaypoint)){
                int hash = Objects.hash(oldWaypoint.nodeID(), currentWaypoint.nodeID());
                if (hashRouteMap.containsKey(hash)){
                    singleRoutes.add(hashRouteMap.get(hash));
                }
                else{
                    Route singleRoute = routeComputer.bestRouteBetween(oldWaypoint.nodeID(), currentWaypoint.nodeID());
                    singleRoutes.add(singleRoute);
                    hashRouteMap.put(hash, singleRoute);
                }
            }

            oldWaypoint = currentWaypoint;
        }

            if (!singleRoutes.contains(null)) {
                route.setValue(new MultiRoute(singleRoutes));
                elevationProfile.setValue(ElevationProfileComputer.elevationProfile(route.get(), 5));
            } else {
                route.setValue(null);
                elevationProfile.setValue(null);
            }

        }


    /**
     * returns the index of the segment containing it, ignoring empty segments
     * @param position position along the route
     * @return the index
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route.get().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypoints.get(i).nodeID();
            int n2 = waypoints.get(i + 1).nodeID();
            if (n1 == n2) index += 1;
        }
        return index;
    }


    public double highlightedPosition(){
        return highlightedPosition.get();
    }

    public DoubleProperty getHighlightedPositionP(){
        return highlightedPosition;
    }

    public void setHighlightedPositionProperty(double value){
        highlightedPosition.set(value);
    }

    public ObservableList<Waypoint> getWaypoints(){
        return waypoints;
    }

    public ReadOnlyObjectProperty<Route> getRouteProperty() {
        return route;
    }

    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfileProperty() {
        return elevationProfile;
    }

}
