'use strict';

angular.
  module('moneySaverApp').
  controller('HeaderController', ['$scope','$location', function($scope,$location) {
    $scope.isActive = function (viewLocation) {
      return viewLocation === $location.path(); 
    }
  }]).
  controller('FooterController', ['$scope','$location','$anchorScroll',function($scope,$location,$anchorScroll) {
    $scope.footerVisible = false;
    $scope.goToTop = function() {
      var old = $location.hash();
      $location.hash('pagebody');
      $anchorScroll();
      //workaround to avoid ngroute interaction in a simple way
      $location.hash(old);
    }
  }]).
  directive("detectscroll", function ($window) {
  	return function(scope, element, attrs) {
  		angular.element($window).bind("scroll", function() {
  			if (this.pageYOffset >= 100) {
                 scope.footerVisible = true;
            } else {
                 scope.footerVisible = false;
            }
            scope.$apply();
  		});
  	}
  });