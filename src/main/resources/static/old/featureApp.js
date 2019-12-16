import Map from '/map';

const {
  RSocketClient,
  JsonSerializer,
  IdentitySerializer
} = require('rsocket-core');

document.addEventListener("DOMContentLoaded", function() {
  let mapElement = document.getElementById('map');

  Map.loadGoogleMapsApi().then(function(googleMaps) {
    Map.createMap(googleMaps, mapElement);
  });
});

const RSocketWebSocketClient = require('rsocket-websocket-client').default;
var client = undefined;

function addErrorMessage(prefix, error) {
  // var ul = document.getElementById("messages");
  // var li = document.createElement("li");
  // li.appendChild(document.createTextNode(prefix + error));
  // ul.appendChild(li);
}

function reloadMessages(message) {
  var ul = document.getElementById("messages");
  var all_li = ul.getElementsByTagName("li");

  for (let i = 0; i < all_li.length; i++) {
    const li = all_li[i];
    if (li.innerText.includes(message['id']))
      return;
  }

  var li = document.createElement("li");
  li.appendChild(document.createTextNode(JSON.stringify(message)));
  ul.appendChild(li);
}

function main() {
  if (client !== undefined) {
    client.close();
 //   document.getElementById("messages").innerHTML = "";
  }

  // Create an instance of a client
  client = new RSocketClient({
    serializers: {
      data: JsonSerializer,
      metadata: IdentitySerializer
    },
    setup: {
      // ms btw sending keepalive to server
      keepAlive: 60000,
      // ms timeout if no keepalive response
      lifetime: 180000,
      // format of `data`
      dataMimeType: 'application/json',
      // format of `metadata`
      metadataMimeType: 'message/x.rsocket.routing.v0',
    },
    transport: new RSocketWebSocketClient({
      url: 'ws://localhost:8888/traveltime-message'
    }),
  });


  // Open the connection
  client.connect().subscribe({    onComplete: socket => {
      // socket provides the rsocket interactions fire/forget, request/response,
      // request/stream, etc as well as methods to close the socket.
      socket.requestStream({
        data: {
          //   'author': document.getElementById("author-filter").value
        },
        metadata: String.fromCharCode('traveltime-message'.length) + 'traveltime-message',
      }).subscribe({
        onComplete: () => console.log('complete'),
        onError: error => {
          console.log(error);
          addErrorMessage("Connection has been closed due to ", error);
        },
        onNext: payload => {
          console.log(payload.data);
         // reloadMessages(payload.data);
        },
        onSubscribe: subscription => {
          subscription.request(2147483647);
        },
      });
    },
    onError: error => {
      console.log(error);
      addErrorMessage("Connection has been refused due to ", error);
    },
    onSubscribe: cancel => {
      /* call cancel() to abort */
    }
  });
}

function createRoadPopup(feature, pos) {

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

  // let i = line.localID;
  // line.setOptions({strokeWeight: 6});
  // for (j in eventFeatures) {
  //     if (j !== i) {
  //         eventFeatures[j].line.setOptions({strokeWeight: eventFeatures[j].line.originalWeight});
  //     }
  // }
}

let data, eventFeatures, i;
let clickListener = [];
let loadedFeatures;

function roadEventListener() {

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

    i = 0;
    for (i in eventFeatures) {
      let f;
      f = eventFeatures[i];
      ///////////////////////////////////////////////////////////
      let color = speedToColor(f.properties.Type, f.properties.Velocity);

      if (f.properties.Velocity > 0) {
        var weight = 3;
      } else {
        var weight = 1;
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

document.addEventListener('DOMContentLoaded', main);
//document.getElementById('author-filter').addEventListener('change', main);