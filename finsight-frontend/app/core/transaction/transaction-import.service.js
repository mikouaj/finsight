'use strict';

angular.
  module('core.transaction').
  factory('TransactionImportTypes', ['$resource',
    function($resource) {
      return $resource('http://127.0.0.1:8090/transactions/import', {}, {
        query: {
          method: 'GET',
          isArray: true
        }
      });
    }
  ]);