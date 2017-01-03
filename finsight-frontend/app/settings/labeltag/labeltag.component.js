'use strict';

angular.
  module('settings').
  component('labeltag', {
    templateUrl: 'settings/labeltag/labeltag.template.html',
    controller: ['Backend','$scope','$uibModal',function LabelTagController(Backend,$scope,$uibModal) {
      var self = this;
      self.labels=[];
      self.labelsX=[];
      
      Backend.getApi().then(function(api) {
      	api.labels.get().then(function(response) {
      		self.labels = response.data;
          for(var id in response.data) {
            self.labelsX[response.data[id].uri] = response.data[id];
          }
      	});
      });

      self.remove = function(index) {
        Backend.getApi().then(function(api) {
          api.labels.delete(self.rules[index]).then(function() {
            self.labels.splice(index,1);
          });
        });
        self.closeConfirmModal();
      }

      self.update = function(data,label) {
      }

      self.openConfirmModal = function(index) {
        $scope.index = index;
        self.modalInstance = $uibModal.open({
          animation: 'true',
          templateUrl: 'settings/labeltag/confirmation-modal.template.html',
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
        if(self.inserted == self.labels[index]) {
          self.labels.splice(index,1);
        }
      }

      self.checkEmpty = function(data) {
        if(!data) {
          return "can't be empty !";
        }
      }

      self.add = function() {
        self.inserted = {
          regexp: '',
          active: false,
          labels: []
        }
        self.labels.push(self.inserted);
      }
    }]
  });