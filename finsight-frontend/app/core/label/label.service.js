'use strict';

angular.
  module('core.label').
  factory('Label', ['$resource',
    function($resource) {
      return $resource('http://127.0.0.1:8090/labels/:id', {}, {
        query: {
          method: 'GET',
          isArray: true
        }
      });
    }
  ]);