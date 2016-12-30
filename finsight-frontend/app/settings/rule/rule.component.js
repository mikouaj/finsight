'use strict';

angular.
  module('settings').
  component('rule', {
    templateUrl: 'settings/rule/rule.template.html',
    controller: ['Backend','$scope','$uibModal',function RuleController(Backend,$scope,$uibModal) {
      var self = this;
      self.labels=[];
      self.labelsX=[];
      
      Backend.getApi().then(function(api) {
      	api.labelRules.get().then(function(response) {
      		self.rules = response.data;
          api.labels.get().then(function(response) {
            self.labels=response.data;
            for(var id in response.data) {
              self.labelsX[response.data[id].uri] = response.data[id];
            }
          });
      	});
      });

      self.remove = function(index) {
        Backend.getApi().then(function(api) {
          //using force delete (will worh even with labels assigned to the rule)
          api.labelRules.delete(self.rules[index],{params:{force:"true"}}).then(function() {
            self.rules.splice(index,1);
          });
        });
        self.closeConfirmModal();
      }

      self.update = function(index,data) {
        var selfA = self.rules[index];
        console.log(data);

      }

      self.openConfirmModal = function(index) {
        $scope.index = index;
        self.modalInstance = $uibModal.open({
          animation: 'true',
          templateUrl: 'settings/rule/confirmation-modal.template.html',
          scope: $scope,
          size: 'sm'
        });
      }

      self.closeConfirmModal = function() {
        if(self.modalInstance) {
          self.modalInstance.close();
        }
      }

      self.cancelUpdate = function(index) {
        if(self.inserted == self.rules[index]) {
          self.rules.splice(index,1);
        }
      }

      self.checkEmpty = function(data) {
        if(!data) {
          return "can't be empty !";
        }
      }
    }]
  });