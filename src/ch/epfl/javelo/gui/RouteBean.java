package ch.epfl.javelo.gui;

import ch.epfl.javelo.LRUCache;
import ch.epfl.javelo.routing.*;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public final class RouteBean{
    private final RouteComputer routeComputer;
    public static ObservableList<Waypoint> waypoints;
    public ObjectProperty<Route> route;
    public DoubleProperty highlightedPosition = new SimpleDoubleProperty();
    public ObjectProperty<ElevationProfile> elevationProfile;
    private final Map<Integer, Route> hashRouteMap = new LRUCache<>(5, 0.75f);

    public RouteBean(RouteComputer routeComputer){
        this.routeComputer = routeComputer;
        waypoints = FXCollections.observableArrayList();
        route = new SimpleObjectProperty<>();
        elevationProfile = new SimpleObjectProperty<>();

        waypoints.addListener((InvalidationListener)  e -> recalculateRouteAndProfile());
    }


    public static void changeWayPoints(ObservableList<Waypoint> list) {
        //waypoints.clear();
        waypoints.addAll(list);
    }

    private void recalculateRouteAndProfile(){
        List<Route> singleRoutes = new ArrayList<>();
        Iterator<Waypoint> it = waypoints.listIterator();
        Waypoint oldWaypoint = it.next();
        Waypoint currentWaypoint;

        while (it.hasNext()){
            currentWaypoint = it.next();

            int hash = Objects.hash(oldWaypoint.nodeID(), currentWaypoint.nodeID());

            if (hashRouteMap.containsKey(hash)){
                singleRoutes.add(hashRouteMap.get(hash));
            }

            else{
                Route singleRoute = routeComputer.bestRouteBetween(oldWaypoint.nodeID(), currentWaypoint.nodeID());
                singleRoutes.add(singleRoute);
                hashRouteMap.put(hash, singleRoute);
            }

            oldWaypoint = currentWaypoint;
        }

        if(waypoints.size() >= 2 && !singleRoutes.contains(null)) {
            route.setValue(new MultiRoute(singleRoutes));
            elevationProfile.setValue(ElevationProfileComputer.elevationProfile(route.get(), 5));
        }
        else{
            route.setValue(null);
            elevationProfile.setValue(null);
        }
    }


    public double highlightedPosition(){
        return highlightedPosition.get();
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

    private static class Pair{
        int first;
        int second;

        public Pair(int f, int s) {
            first = f;
            second = s;
        }
    }
}
