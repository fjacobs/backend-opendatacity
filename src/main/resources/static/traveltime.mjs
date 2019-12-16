// ----------- TravelTime --------------------------

import map from "./map";

export function createRoadPopup(map, feature, pos) {

    let html = "<div>Naam:" + feature.getProperty("Name") + "</div>";
    html += "<div>ID : " + feature.getId()+ "</div>";
    html += "<div>Lengte:  " + feature.getProperty("Length") + " meter</div>";
    html += "<div>Snelheid: " + feature.getProperty("Velocity") + " km/u</div>";
    html += "<div>Huidige reistijd: " + Math.floor(feature.getProperty("Traveltime") / 60) + ":" + ("0" + eventFeatures[i].properties.Traveltime % 60).slice(-2) + "</div>";
    html += "<div>Timestamp: " + feature.getProperty("Timestamp") + "</div>";

    let infowindow = new google.maps.InfoWindow();

    infowindow.setContent("<div>" + html + "</div>");
    infowindow.setPosition(pos);
    infowindow.setOptions({pixelOffset: new google.maps.Size(0, -30)});
    infowindow.open(map);

    map.data.setStyle( function (feature) {

        return {
            strokeColor: color,
            strokeOpacity: 1.0,
            strokeWeight: weight,
            title: feature.getProperty("Name"),
            localID: i,
            originalWeight: weight
        };
    });

    let i = line.localID;
    line.setOptions({strokeWeight: 6});
    for (let j in eventFeatures) {
        if (j !== i) {
            eventFeatures[j].line.setOptions({strokeWeight: eventFeatures[j].line.originalWeight});
        }
    }
}

export function roadEventListener(map, data ) {

    let sse;
    if (!!window.EventSource) {
        sse = new EventSource('http://localhost:9090/roadSubscription');
    } else {
        // Error, resorting to xhr polling
    }

    let eventNumber = 0;
    let polyLines = [];
    sse.addEventListener('event', function (e) {
        console.log("Receiving event number: " + eventNumber);
        let result = jQuery.parseJSON(e.data);
        eventFeatures = result.features;
        console.log("Received: " + eventFeatures.length);
        console.log(eventFeatures[50].properties.Velocity);
        if (eventNumber === 0) {
            // loadedFeatures = map.data.addGeoJson(result);
            console.log("Should only be called once");
            loadedFeatures = map.data.addGeoJson(result);
            map.data.addListener('click', function(event) {
                createRoadPopup(event, event.latLng);

                // let infowindow = new google.maps.InfoWindow();
                // infowindow.setContent("hellooooooooooooo: " + event.feature.getId());
                // infowindow.setPosition(event.latLng);
                // infowindow.setOptions({pixelOffset: new google.maps.Size(0,-34)});
                // infowindow.open(map);
            });
        }

        let i = 0;
        for (i in eventFeatures) {
            let f;
            f = eventFeatures[i];
            ///////////////////////////////////////////////////////////
            let color = speedToColor(f.properties.Type, f.properties.Velocity);

            if (f.properties.Velocity > 0) {
                let weight = 3;
            } else {
                let weight = 1;
            }
            /////////////////////////////////////////////////////////
            let temp = map.data.getFeatureById(f.id);
            temp.forEachProperty(function (value, key) {
                if (temp.getProperty(key) !== undefined) {
                    temp.setProperty(eventFeatures[i].properties[key], eventFeatures[i].properties[value]);
                }
            });

            // f.line = new google.maps.Polyline({map: map, path: path, strokeColor: color, strokeOpacity: 1.0,strokeWeight: weight, title: f.properties.Name, localID: i, originalWeight: weight});
            // google.maps.event.addListener(temp, 'click', function(){ showFeature(this) });

            // let listener = google.maps.event.addListener(temp.geometry.latLng,'mouseover',  function (event) {
            //     createRoadPopup(this, event);
            // });

            map.data.setStyle( function (temp) {
                return {
                    strokeColor: color,
                    strokeOpacity: 1.0,
                    strokeWeight: weight,
                    title: temp.getProperty("Name"),
                    localID: i,
                    originalWeight: weight
                };
            });
        }
        eventNumber++;
    }, false);

    sse.addEventListener('open', function (e) {
        console.log("connection was opened");
    }, false);

    sse.addEventListener('error', function (e) {
        if (e.readyState === EventSource.CLOSED) {
            console.log("Received 'error event: connection was closed");
        }
    }, false);
}

//'http://localhost:9090/roadfullcollection'

export function loadTravelTimePoll(map, url) {

    $.getJSON(url, function (result) {
        eventFeatures = result.features;

        //console.log(eventFeatures.length);

        for (i in eventFeatures) {
            let feature = eventFeatures[i];
            let color = speedToColor("H", feature.properties.Velocity);
            let points = feature.geometry.coordinates;
            let latLng = [];

            for (let j in points) {
                if (!isNaN(points[j][1])) {
                    latLng.push(new google.maps.LatLng(points[j][1], points[j][0]));
                }
            }

            if (feature.properties.Velocity > 0) {
                let weight = 3;
            } else {
                let weight = 1;
            }

            feature.line = new google.maps.Polyline({
                map: map,
                path: latLng,
                strokeColor: color,
                strokeOpacity: 1.0,
                strokeWeight: weight,
                title: feature.properties.Name,
                localID: i,
                originalWeight: weight
            });

            clickListener[i] = feature.line.addListener('click', function (event) {
                createRoadPopup(this, event.latLng);
            });
        }
    });
}

export function speedToColor(type, speed) {
    let speedColors;
    if (type === "H") {
        //Snelweg
        speedColors = {
            0: "#D0D0D0",
            1: "#BE0000",
            30: "#FF0000",
            50: "#FF9E00",
            70: "#FFFF00",
            90: "#AAFF00",
            120: "#00B22D"
        };
    } else {
        //Overige wegen
        speedColors = {
            0: "#D0D0D0",
            1: "#BE0000",
            10: "#FF0000",
            20: "#FF9E00",
            30: "#FFFF00",
            40: "#AAFF00",
            70: "#00B22D"
        };
    }
    let currentColor = "#D0D0D0";
    for (let i in speedColors) {
        if (speed >= i) currentColor = speedColors[i];
    }
    return currentColor;
}
