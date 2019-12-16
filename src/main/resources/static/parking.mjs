

export default function loadParking(map, parkingUrl) {

    map.data.loadGeoJson(parkingUrl);
    document.getElementById('id').innerHTML = 'no errors';
    map.data.addListener('click', function (event) {
        let infowindow = new google.maps.InfoWindow();
        var myHTML = "<strong>Parking:</strong>" +
            event.feature.getProperty("name") +
            "<br><strong>Beschikbare plekken:</strong> " +
            event.feature.getProperty("freeSpaceShort");

        infowindow.setContent("<div>" + myHTML + "</div>");
        infowindow.setPosition(event.feature.getGeometry().get());
        infowindow.setOptions({pixelOffset: new google.maps.Size(0, -30)});
        infowindow.open(map);
    });

    map.data.setStyle(function (feature) {
        if (String(feature.getProperty("type")) === "parkinglocation") {
            return {icon: "images/parking.png"};
        }
    });
}