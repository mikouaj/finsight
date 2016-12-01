'use strict';

angular.
  module('transactionList').
  component('transactionList', {
    templateUrl: 'transaction-list/transaction-list.template.html',
    controller: ['Backend','$http','$q',
    function TransactionListController(Backend,$http,$q) {
        var self = this;

        self.labelCache = [];
        self.labelPromises = [];
        Backend.getApi().then(function(api) {
          api.transactions.get().then(function(transactions) {
            self.transactions=transactions.data;
            angular.forEach(self.transactions, function(transaction) {
              transaction.labelData=[];
              angular.forEach(transaction.labels, function(labelURI) {
                var labelId = labelURI.split('/').pop();
                if(!(labelId in self.labelCache)) {
                  self.labelCache[labelId] = {};
                  self.labelPromises.push(api.labels.getById({"id":labelId}).then(function (label) {
                    return label.data;
                  }));
                }
              });
            });
          }).then(function() {
            $q.all(self.labelPromises).then(function(objects) {
              for(var id in objects) {
                self.labelCache[objects[id].id] = objects[id];
              }
              for(var id in self.transactions) {
                for(var labelURIId in self.transactions[id].labels) {
                  var labelId = self.transactions[id].labels[labelURIId].split('/').pop();
                  self.transactions[id].labelData.push(self.labelCache[labelId]);
                }
              }
            });
          });
        });

        self.displayCurrency = 'PLN';
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

        self.typeOptions = [ "", "CardOperation","Transfer","Commission"];
        self.type = self.typeOptions[0];

        self.orderProp = 'date';

        self.toggleDetails = function(transaction) {
          if(!transaction.details) {
             $http.get(transaction.url).then(function(response) {
               transaction.details = response.data;
             });
          }
          transaction.collapsed = !transaction.collapsed;
        };

        self.dateFromPopupOpen = function() {
          self.dateFromPopupOpened = true;
        }

        self.dateToPopupOpen = function() {
          self.dateToPopupOpened = true;
        }

        self.searchQueryClear = function() {
          self.searchQuery="";
        }
      }
    ]
  });