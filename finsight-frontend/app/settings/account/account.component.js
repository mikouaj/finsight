'use strict';

angular.
  module('settings').
  component('account', {
    templateUrl: 'settings/account/account.template.html',
    controller: ['Backend','$scope','$uibModal',function AccountController(Backend,$scope,$uibModal) {
      var self = this;
      Backend.getApi().then(function(api) {
      	api.accounts.getAccounts().then(function(response) {
      		self.accounts = response.data;
      	});
      });

      self.removeAccount = function(index) {
        Backend.getApi().then(function(api) {
          api.accounts.delete(self.accounts[index]).then(function() {
            self.accounts.splice(index,1);
          });
        });
        self.closeConfirmModal();
      }

      self.updateAccount = function(data,account) {
        Backend.getApi().then(function(api) {
          if(typeof account.id === 'undefined') {
            api.accounts.createAccount(data).then(function(response) {
              account.id = response.data.id;
            });
          } else {
            angular.extend(data,{id:account.id});
            return api.accounts.replace(data);
          }
        });
      }

      self.openConfirmModal = function(index) {
        $scope.index = index;
        self.modalInstance = $uibModal.open({
          animation: 'true',
          templateUrl: 'settings/account/confirmation-modal.template.html',
          scope: $scope,
          size: 'sm'
        });
      }

      self.closeConfirmModal = function() {
        if(self.modalInstance) {
          self.modalInstance.close();
        }
      }

      self.addAccount = function() {
        self.inserted = {
          number: '',
          name: ''
        }
        self.accounts.push(self.inserted);
      }

      self.cancelUpdate = function(index) {
        if(self.inserted == self.accounts[index]) {
          self.accounts.splice(index,1);
        }
      }

      self.checkEmpty = function(data) {
        if(!data) {
          return "can't be empty !";
        }
      }
    }]
  });