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

      self.update = function(data,rule) {
        return Backend.getApi().then(function(api) {
          var promise;
          var labelsToRemove=[];
          var labelsToAdd=[];
          for(var id in rule.labels) {
            var labelId = rule.labels[id].split('/').pop();
            labelsToRemove[labelId]=1;
          }
          for(var id in data.labels) {
            var labelId = data.labels[id].split('/').pop();
            if(labelId in labelsToRemove) {
              labelsToRemove.splice(labelId,1);
            } else {
              labelsToAdd[labelId] = 1;
            }
          }

          var updateLabels = function() {
            var promises=[];
            for(var key in labelsToRemove) {
              promises.push(api.labelRules.removeLabel({id:rule.id,labelId:key}));
            }
            for(var key in labelsToAdd) {
              promises.push(api.labelRules.addLabel({id:rule.id,labelId:key}));
            }
            return promises;
          }

          data.labels=undefined;
          if(typeof rule.id === 'undefined') {
            promise = api.labelRules.create(data).then(function(response) {
              rule.id = response.data.id;
              updateLabels();
              return true;
            },function(response) {
              return response.statusText;
            });
          } else {
            angular.extend(data,{id:rule.id});
            promise = api.labelRules.replace(data).then(function(response) {
              updateLabels();
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

      self.add = function() {
        self.inserted = {
          regexp: '',
          active: false,
          labels: []
        }
        self.rules.push(self.inserted);
      }
    }]
  });