'use strict';

angular.
  module('settings').
  component('rule', {
    templateUrl: 'settings/rule/rule.template.html',
    controller: ['Backend','$scope','$uibModal','$q',function RuleController(Backend,$scope,$uibModal,$q) {
      var self = this;
      
      Backend.getApi().then(function(api) {
      	api.labelRules.get().then(function(response) {
      		self.rules = response.data;
          api.labels.get().then(function(response) {
            self.labels=[];
            for(var id in response.data) {
              self.labels[response.data[id].uri] = response.data[id];
            }
          });
      	});
      });
    }]
  });