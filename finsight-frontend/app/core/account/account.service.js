'use strict';

angular.
  module('core.account').
  factory('Account', ['$resource',
    function($resource) {
      return $resource('http://127.0.0.1:8090/account/:accountId', {}, {
        query: {
          method: 'GET',
          isArray: true
        }
      });
    }
  ]);