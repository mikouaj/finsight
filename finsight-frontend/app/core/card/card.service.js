'use strict';

angular.
  module('core.card').
  factory('Card', ['$resource',
    function($resource) {
      return $resource('http://127.0.0.1:8090/card/:cardId', {}, {
        query: {
          method: 'GET',
          isArray: true
        }
      });
    }
  ]);