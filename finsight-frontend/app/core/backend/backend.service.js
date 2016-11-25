'use strict';

angular.
  module('core.backend').
  factory('Backend', ['$http','AngularSwaggerific',
    function($http,AngularSwaggerific) {
    	var getApi = function() {
    		$http.get('http://petstore.swagger.io/v2/swagger.json').then(function(response) {
    			return new AngularSwaggerific(response.data);
    		});
    	}
    	return {
    		getApi: getApi
    	}
    }
  ]);