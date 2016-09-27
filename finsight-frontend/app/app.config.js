'use strict';

angular.
  module('moneySaverApp').
  config(['$locationProvider','$routeProvider',
    function config($locationProvider, $routeProvider) {
      $locationProvider.hashPrefix('!');

      $routeProvider.
        when('/transactions', {
          template: '<transaction-list></transaction-list>'
        }).
        when('/settings', {
          template: '<settings></settings>'
        }).
        otherwise('/transactions');
    }
  ]);