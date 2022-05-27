<h1>Javelo</h1>

<h3><u>Section and Course: </u></h3> 
<p>IC Bachelor 2 at EPFL, "Programmation Orientée Objet" (CS108) </p>

<h3><u> Authors: </u></h3>
<ul>
    <em>
 <li>David Farah </li>
 <li> Wesley Nana Davies </li>
    </em>
</ul>

<h3><u> Project Description: </u></h3>
<p>
This Project is a Swiss bike route planner. <br>
JaVelo's interface is similar to online planners like Google Maps. 
JaVelo is not, however, a web application,
it's a Java application that runs exclusively on the computer of the person using it. 
The user also has the ability to export the route as a gpx file for mobile use.
<br><br>
Planning a route is done by placing at least two waypoints (the start point and the end point) by clicking on the map. 
As soon as two of these points have been placed, JaVelo determines the route connecting these two points which it 
considers ideal for a person traveling by bicycle. To do this, it takes into account not only the type of roads taken 
(by favoring minor roads, cycle paths and other factors) but also the terrain by avoiding steep climbs.
<br><br>
As soon as a route has been calculated, its longitudinal profile is displayed at the bottom of the interface, along with 
some statistics: total length, positive and negative elevations, etc. When the mouse pointer is over a point on the 
profile, the corresponding point on the route is highlighted on the map, and vice versa.
<br><br>
Finally, it is possible to modify an existing route, by adding, deleting or moving waypoints. Each change causes the 
recalculation of the ideal route and its longitudinal profile.
</p>

<h3><u> Limitations: </u></h3>
<p>
JaVelo is limited to Swiss territory, as there is currently no digital elevation model covering the whole Earth that is 
accurate enough for our needs and available free of charge. For Switzerland, such a model exists since the federal office
of topography (swisstopo) has recently offered free access to all of its data, including the very precise 
SwissALTI3D altimetric model
</p>
