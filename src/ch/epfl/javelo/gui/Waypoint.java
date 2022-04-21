package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * represents a waypoint
 */
public record Waypoint(PointCh point, int nodeID) { }
