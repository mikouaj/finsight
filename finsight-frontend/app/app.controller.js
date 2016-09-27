'use strict';

angular.
  module('moneySaverApp').
  controller('HeaderController', ['$scope','$location', function($scope,$location) {
    $scope.isActive = function (viewLocation) {
      return viewLocation === $location.path(); 
    }
  }]);