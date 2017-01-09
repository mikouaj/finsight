'use strict';

angular.
  module('settings').
  component('labeltag', {
    templateUrl: 'settings/labeltag/labeltag.template.html',
    controller: ['Backend','$scope','$uibModal',function LabelTagController(Backend,$scope,$uibModal) {
      var self = this;
      self.labels=[];
      self.labelsHash={};
      
      Backend.getApi().then(function(api) {
      	api.labels.get().then(function(response) {
      		self.labels = response.data;
          for(var id in response.data) {
            self.labelsHash[response.data[id].id] = response.data[id];
          }
      	});
      });

      self.remove = function(index) {
        Backend.getApi().then(function(api) {
          api.labels.delete(self.labels[index]).then(function() {
            self.labels.splice(index,1);
          });
        });
        self.closeConfirmModal();
      }

      self.update = function(data,label) {
        return Backend.getApi().then(function(api) {
          var promise;
          if(typeof label.id === 'undefined') {
            promise = api.labels.create(data).then(function(response) {
              label.id = response.data.id;
              return true;
            },function(response) {
              return response.statusText;
            });
          } else {
            angular.extend(data,{id:label.id});
            promise = api.labels.replace(data).then(function(response) {
              return true;
            },function(response) {
              return response.statusText;
            });
          }
          return promise;        
        });
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

      self.filterSelfParent = function(actual, expected) {
         return !angular.equals(actual, expected);
      }
    }]
  });