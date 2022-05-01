package ch.epfl.javelo.gui;

import ch.epfl.javelo.LRUCache;
import ch.epfl.javelo.routing.*;
import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.util.Pair;

import java.beans.Beans;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class RouteBean{
    private final RouteComputer rc;
    public static ObservableList<Waypoint> waypoints;
    public ObjectProperty<Route> route;
    public DoubleProperty highlightedPosition = new SimpleDoubleProperty();
    public ObjectProperty<ElevationProfile> elevationProfile;
    private final Map<Pair, Route> pairRouteMap = new LRUCache<>(5, 0.75f);

    public RouteBean(RouteComputer rc){
        this.rc = rc;
        waypoints = FXCollections.observableArrayList();
        route = new SimpleObjectProperty<>();
        elevationProfile = new SimpleObjectProperty<>();
        waypoints.addListener((InvalidationListener)  e -> initializeAttributes());

    }


    public static void changeWayPoints(ObservableList<Waypoint> list) {
        //waypoints.clear();
        waypoints.addAll(list);
    }

    private void initializeAttributes(){
        System.out.println("Initializing attributes");
        List<Route> sr = new ArrayList<>();
        Iterator<Waypoint> it = waypoints.listIterator();
        System.out.println(waypoints.size());
        Waypoint previousW = it.next();
        Waypoint currentW;

        while (it.hasNext()){
            currentW = it.next();
            Pair p = new Pair(previousW.nodeID(), currentW.nodeID());
            if (pairRouteMap.containsKey(p)){
                sr.add(pairRouteMap.get(p));
            }
            else{
                Route singleRoute = rc.bestRouteBetween(previousW.nodeID(), currentW.nodeID());
                sr.add(singleRoute);
                pairRouteMap.put(p, singleRoute);
            }
            previousW = currentW;
        }

        if(waypoints.size() >= 2 && !sr.contains(null)) {
            System.out.println("setting values");
            route.setValue(new MultiRoute(sr));
            elevationProfile.setValue(ElevationProfileComputer.elevationProfile(route.get(), 5));
        }

    }

    public DoubleProperty highlightedPositionProperty(){
        return highlightedPosition;
    }

    public double highlightedPosition(){
        return highlightedPosition.get();
    }

    public void setHighlightedPositionProperty(double value){
        highlightedPosition.set(value);
    }

    public ReadOnlyObjectProperty<Route> getRouteProperty() {
        return route;
    }

    public ReadOnlyObjectProperty<ElevationProfile> getElevationProfileProperty() {
        return elevationProfile;
    }

    public ObservableList<Waypoint> getWaypoints(){
        return waypoints;
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
