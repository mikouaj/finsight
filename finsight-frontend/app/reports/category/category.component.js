'use strict';

angular.
  module('reports').
  component('categoryreport', {
    templateUrl: 'reports/category/category.template.html',
    controller: ['Backend','$scope','$uibModal',function CategoryReportController(Backend,$scope,$uibModal) {
      var self = this;

      var transactionCache={};

      Backend.getApi().then(function(api) {
        api.labels.get().then(function(response) {
          self.labels = response.data;
        });
      });

      self.dateFormat = 'yyyy-MM-dd';
      
      self.dateFrom = new Date(2000,1,1);
      self.dateFromPopupOpened = false;
      self.dateFromOptions = {
        formatYear: 'yy',
        maxDate: new Date(),
        minDate: new Date(2000,1,1),
        startingDay: 1
      };

      self.dateTo = new Date();
      self.dateToPopupOpened = false;
      self.dateToOptions = {
        formatYear: 'yy',
        maxDate: new Date(),
        minDate: self.dateFrom,
        startingDay: 1
      };

      self.dateFromPopupOpen = function() {
        self.dateFromPopupOpened = true;
      }

      self.dateToPopupOpen = function() {
        self.dateToPopupOpened = true;
      }


      self.labelSelected = function labelSelected(item,model) {
        for(var id in model.transactions) {
          var transactionURL = model.transactions[id];
          console.log("fetching transaction "+transaction.id)
         /* if(typeof transactionCache[transaction.id] === 'undefined') {
            console.log("fetching transaction "+transaction.id)
          }*/
        }
      }
    }]
  });