'use strict';

angular.
  module('transactionList').
  component('transactionList', {
    templateUrl: 'transaction-list/transaction-list.template.html',
    controller: ['Backend','$http','$q','$location',
    function TransactionListController(Backend,$http,$q,$location) {
        var self = this;

        self.labelsHash={};
        Backend.getApi().then(function(api) {
          api.transactions.get().then(function(transactions) {
            self.transactions=transactions.data;
            api.labels.get().then(function(response) {
              self.labels=response.data;
              for(var id in response.data) {
                self.labelsHash[response.data[id].id] = response.data[id];
              }
            });
          });
        });

        self.displayCurrency = 'PLN';
        self.dateFormat = 'yyyy-MM-dd';

        self.dateFrom = new Date();
        self.dateFrom.setMonth(self.dateFrom.getMonth()-1);
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

        self.update = function(data,transaction) {
          return Backend.getApi().then(function(api) {
            angular.extend(data,{id:transaction.id});
            return api.transactions.replace(data).then(function(response) {
              return true;
            },function(response) {
              return response.statusText;
            });
          });
        }

        self.newRule = function(transaction) {
          var ruleStr = transaction.title;
          if(transaction.type=='CardOperation') {
            ruleStr = transaction.details.destination;
          } else if(transaction.type=='Transfer') {
            ruleStr = transaction.details.description;
          }
          $location.path( '/settings' ).search({newRule: encodeURIComponent(ruleStr)});
        }
      }
    ]
  });
