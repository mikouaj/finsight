'use strict';

angular.
  module('transactionList').
  filter('internalTransfer', function() {
    return function(input) {
      return input ? 'Internal' : '';
    };
  })
  .filter('dateRange', function() {
    return function(items,dateFrom,dateTo) {
      var filtered=[];
      angular.forEach(items, function(item) {
        if(dateFrom < new Date(item.date) && dateTo > new Date(item.date)) {
          filtered.push(item);
        }
      });
      return filtered;
    };
  });