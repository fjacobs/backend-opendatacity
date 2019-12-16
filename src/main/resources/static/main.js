
mapstyle =  [{"featureType":"administrative","elementType":"all","stylers":[{"saturation":"-100"}]},{"featureType":"administrative.province","elementType":"all","stylers":[{"visibility":"off"}]},{"featureType":"landscape","elementType":"all","stylers":[{"saturation":-100},{"lightness":65},{"visibility":"on"}]},{"featureType":"poi","elementType":"all","stylers":[{"saturation":-100},{"lightness":"50"},{"visibility":"simplified"}]},{"featureType":"road","elementType":"all","stylers":[{"saturation":"-100"}]},{"featureType":"road.highway","elementType":"all","stylers":[{"visibility":"simplified"}]},{"featureType":"road.arterial","elementType":"all","stylers":[{"lightness":"30"}]},{"featureType":"road.local","elementType":"all","stylers":[{"lightness":"40"}]},{"featureType":"transit","elementType":"all","stylers":[{"saturation":-100},{"visibility":"simplified"}]},{"featureType":"water","elementType":"geometry","stylers":[{"hue":"#ffff00"},{"lightness":-25},{"saturation":-97}]},{"featureType":"water","elementType":"labels","stylers":[{"lightness":-25},{"saturation":-100}]}];
parkingUrl = 'http://localhost:9090/getCustomParkingJson';
traveltimeUrl = 'http://localhost:9090/roadSubscriptionTest';
pollSingle = 'http://localhost:9090/getRoadSingle';

import MapApi from "./map.mjs"
//import rsocketConnection from "./rsocket"
let map;

// function callApi() {
//     // use static class function to run npm package to make api request
//     MapApi.loadGoogleMapsApi()
//         .then(res => initApp())
//         .catch(e => console.log("api fetch unsuccessful: " + e));
// }
//
// function initApp() {
//     map = new google.maps.Map( document.getElementById('map'), {
//         zoom: 13,
//         center: new google.maps.LatLng(52.37063538130748, 4.899999460629829),
//         mapTypeId: google.maps.MapTypeId.TERRAIN,
//         styles: mapstyle
//     });
// }

function main() {
    MapApi.hello();
}

