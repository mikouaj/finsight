'use strict';

angular.
  module('core.backend').
  factory('Backend', ['$http','AngularSwaggerific',
    function($http,AngularSwaggerific) {
    	var getApi = function() {
        return $http.get('http://127.0.0.1:8090/api/swagger.json').then(function(response) {
          return new AngularSwaggerific(response.data);
        });
    	}
      var getTransactionImportURL = function() {
        return 'http://127.0.0.1:8090/api/transactions/import';
      }
    	return {
    		getApi: getApi,
        getTransactionImportURL : getTransactionImportURL
    	}
    }
  ]);