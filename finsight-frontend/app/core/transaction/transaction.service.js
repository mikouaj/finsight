'use strict';

angular.
  module('core.transaction').
  factory('Transaction', ['$resource',
    function($resource) {
      return $resource('http://127.0.0.1:8090/transactions/:transactionId', {}, {
        query: {
          method: 'GET',
          isArray: true
        }
      });
    }
  ]);