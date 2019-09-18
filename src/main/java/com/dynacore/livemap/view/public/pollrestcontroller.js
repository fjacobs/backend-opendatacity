
var app = angular.module('opendata', ['ngMap']);


app.service('locationService', function() {
	  var locationList = [];

	  var addLocation = function(newObj) {
		  locationList.push(newObj);
	  };

	  var getLocations = function(){
	      return locationList;
	  };

	  return {
	    addLocation: addLocation,
	    getLocations: getLocations
	  };
});

app.controller('LocationController', function($scope, locationService) {
    $scope.callToAddToLocationList = function(currObj){
        $http.get('http://localhost:9090/getCustomParkingJson').success(function (data) {
        	$scope.locations = data;        	
        	        	
        	// locationService.addProduct(currObj);
        });
    };
});




app.factory('parkingIconService', function() {
  statusIcons = [];
  return {
	  statusIcons	
  };
});



app.controller('drawParkingIcons', function($scope, $http, $timeout, parkingIconService) {
		$scope.statusIcon = "/images/parking.png";
	    $scope.counter = 0;
        $timeout(tick, 1000);
		$scope.statusIcons = parkingIconService.statusIcons;
		
        function tick() {	

        	$http.get("http://localhost:9090/getCustomParkingJson").success(function (response) {

        		$scope.parkingList = response.features;

        		for (var i in $scope.parkingList ) {

        		    var parkedPercent =	$scope.parkingList[i].properties.percentage;
        			

        			if ($scope.parkingList[i].properties.state == "error") {
        				$scope.statusIcons[i] = "/images/cancel.svg";
        			} else $scope.statusIcons[i] = "/images/parking.png";

        		}

        		$scope.counter++;				
        		$timeout(tick, 1000);
        	});
        };
});





app.controller('testDrawParkingIcons', function($scope, $http) {
 	$scope.statusIcon = "/images/parking.png";
	
    $http.get("http://localhost:9090/frontEndTest")
    .success(function(response) {$scope.names = response.features;});
});



