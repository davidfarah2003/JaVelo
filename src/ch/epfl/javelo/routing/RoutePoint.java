package ch.epfl.javelo.routing;

import ch.epfl.javelo.projection.PointCh;

public record RoutePoint(PointCh point, double position, double distanceToReference) {


}
