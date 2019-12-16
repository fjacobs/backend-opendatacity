
const {
  RSocketClient,
  JsonSerializer,
  IdentitySerializer
} = require('rsocket-core');

const RSocketWebSocketClient = require('rsocket-websocket-client').default;

var client = undefined;

export default function addErrorMessage(prefix, error) {

  console.log(prefix + error);
}


export default function receiveCallback( feature ) : Object{

  console.log(feature);
}

//traveltime-message
//url='ws://localhost:8888/traveltime-message'
export default function requestStream(url, messageRoute, messageCallback ) {
  if (client !== undefined) {
    client.close();
    // document.getElementById("messages").innerHTML = "";
  }

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
      url: url
    }),
  });

  client.connect()
        .subscribe( { onComplete: socket => {

      socket.requestStream({
        data: {
          //   'author': document.getElementById("author-filter").value
        },
        metadata: String.fromCharCode(messageRoute.length) + messageRoute,
      }).subscribe({
        onComplete: () => console.log('complete'),
        onError: error => {
          console.log(error);
          addErrorMessage("Connection has been closed due to ", error);
        },
        onNext: payload => {
          // payload.data
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


//document.addEventListener('DOMContentLoaded', main);
//document.getElementById('author-filter').addEventListener('change', main);