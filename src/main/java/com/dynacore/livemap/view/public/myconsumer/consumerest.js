function ConsumeRest($scope, $http) {
    $http.get('http://localhost:777/mymvc/triggercamera').
        success(function(data) {
            $scope.triggerCamera = data;
        });
}

