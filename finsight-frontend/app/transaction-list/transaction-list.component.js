'use strict';

angular.
  module('transactionList').
  component('transactionList', {
    templateUrl: 'transaction-list/transaction-list.template.html',
    controller: ['Transaction','$http',
    function TransactionListController(Transaction,$http) {
        var self = this;
        self.transactions=Transaction.query();

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
