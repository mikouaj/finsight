'use strict';

angular.
  module('core.backend').
  factory('Backend', ['$http','$q','AngularSwaggerific',
    function($http,$q,AngularSwaggerific) {
      var self = this;
    	var getApi = function() {
        var deferred = $q.defer();
        if(!self.swaggerific) {
          $http.get('http://127.0.0.1:8090/api/swagger.json').then(function(response) {
            self.swaggerific = new AngularSwaggerific(response.data);
            deferred.resolve(self.swaggerific);
          });
        } else {
          deferred.resolve(self.swaggerific);
        }
        return deferred.promise;
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