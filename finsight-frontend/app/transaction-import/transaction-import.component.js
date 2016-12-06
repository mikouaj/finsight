'use strict';

angular.
  module('transactionImport').
  component('transactionImport', {
    templateUrl: 'transaction-import/transaction-import.template.html',
    controller: ['Backend','Upload','$scope','$uibModal',
    function TransactionImportController(Backend,Upload,$scope,$uibModal) {
      var self = this;

      self.openModal = function openModal() {
       
        self.importTypesResources={};
        self.baseResources=[];
        self.uploadResult="";
        self.selectedType="";
        self.selectedResource="";
        self.selectedFile="";

       Backend.getApi().then(function(api) {
          api.transactions.getImportTypes().then(function(importTypes) {
            self.importTypes = importTypes.data;
            angular.forEach(self.importTypes, function(importType) {
              var $resourcePromise;
              if(importType.baseResourceLink.type=='Account') {
                $resourcePromise = api.accounts.getAccounts();
              } else {
                $resourcePromise = api.cards.getCards();
              }
              $resourcePromise.then(function(resources) {
                self.importTypesResources[importType.id] = resources.data;
              });
            });
          });
        });

        self.importURL = Backend.getTransactionImportURL();
        
        self.modalInstance = $uibModal.open({
          animation: 'true',
          templateUrl: 'transaction-import/import-modal.template.html',
          scope: $scope,
          size: 'lg'
        });
      }

      self.closeModal = function() {
        self.modalInstance.close();
      };

      self.typeChange = function() {
         self.baseResources = self.importTypesResources[self.selectedType];
      };

      self.importEnabled = function() {
        return self.selectedFile && self.selectedType && self.selectedResource;
      }

      self.import = function() {
        if(self.selectedFile) {
          self.uploadResult="";
          Upload.upload({
              url: self.importURL,
              data: {file: self.selectedFile, 'type': self.selectedType, 'baseResourceId': self.selectedResource}
          }).then(function (resp) {
              self.uploadResult = resp.data;
          }, function (resp) {
          }, function (evt) {
              var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
              self.uploadProgress = progressPercentage;
          });
        }
      };
    }]
  });