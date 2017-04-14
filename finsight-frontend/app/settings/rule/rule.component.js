'use strict';

angular.
  module('settings').
  component('rule', {
    templateUrl: 'settings/rule/rule.template.html',
    controller: ['Backend','$scope','$uibModal','$routeParams',
    function RuleController(Backend,$scope,$uibModal,$routeParams) {
      var self = this;
      self.labels=[];
      self.labelsHash={};

      // functions
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
          if(typeof rule.id === 'undefined') {
            promise = api.labelRules.create(data).then(function(response) {
              rule.id = response.data.id;
              return true;
            },function(response) {
              return response.statusText;
            });
          } else {
            angular.extend(data,{id:rule.id});
            promise = api.labelRules.replace(data).then(function(response) {
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

      self.openProcessedModal = function(isSuccess,transactionsCnt,labelsCnt) {
        $scope.isSuccess = isSuccess;
        $scope.transactionsCnt = transactionsCnt;
        $scope.labelsCnt = labelsCnt;
        self.processedModalInstance = $uibModal.open({
          animation: 'true',
          templateUrl: 'settings/rule/processed-modal.template.html',
          scope: $scope,
          size: 'sm'
        });
      }

      self.closeProcessedModal = function() {
        if(self.processedModalInstance) {
          self.processedModalInstance.close();
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

      self.add = function(regexp) {
        self.inserted = {
          regexp: regexp,
          active: false,
          labels: []
        }
        self.rules.push(self.inserted);
      }

      self.searchQueryClear = function() {
        self.searchQuery="";
      }

      self.labelTransactions = function(index) {
        Backend.getApi().then(function(api) {
          api.transactions.runRule({id:self.rules[index].id}).then(function(response) {
            self.openProcessedModal(true,response.data.transactionsCount,response.data.labelsCount);
          },function(response) {
              self.openProcessedModal(false);
          });
        });
      }

      self.labelAllTransactions = function() {
        Backend.getApi().then(function(api) {
          api.transactions.runAllRules().then(function(response) {
            self.openProcessedModal(true,response.data.transactionsCount,response.data.labelsCount);
          },function(response) {
              self.openProcessedModal(false);
          });
        });
      }

      // exec
      Backend.getApi().then(function(api) {
        api.labelRules.get().then(function(response) {
          self.rules = response.data;
          api.labels.get().then(function(response) {
            self.labels=response.data;
            for(var id in response.data) {
              self.labelsHash[response.data[id].id] = response.data[id];
            }
          });

          if($routeParams.hasOwnProperty("newRule")) {
            var ruleStr = decodeURIComponent($routeParams["newRule"]);
            self.add(ruleStr);
          }
        });
      });
    }]
  });
