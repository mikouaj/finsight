'use strict';

angular.
  module('transactionImport').
  component('transactionImport', {
    templateUrl: 'transaction-import/transaction-import.template.html',
    controller: ['TransactionImportTypes','$scope','$resource','$uibModal','Upload',
    function TransactionImportController(TransactionImportTypes,$scope,$resource,$uibModal,Upload) {
      var self = this;

      self.openModal = function openModal() {

       
        self.importTypesResources={};
        self.baseResources=[];
        self.uploadResult="";
        self.selectedType="";
        self.selectedResource="";
        self.selectedFile="";

        self.importTypes=TransactionImportTypes.query({},function success() {
          angular.forEach(self.importTypes, function(importType) {
            var BaseResource = $resource(importType.baseResourceURI);
            self.importTypesResources[importType.id] = BaseResource.query();
          });
        });
        

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
              url: 'http://127.0.0.1:8090/transactions/import',
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